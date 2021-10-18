package ru.spb.snt.aiis.DataValidater;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

public class MeterDataValidater extends Thread{
    long meter_id;
    String meter_name;
//    long connected;
    long data_begin;
    long last_correct_date;
//    List<Long> owner_uspd;
    int req_count=0;
    List<DataHole> dataHoles=new Vector<DataHole>();
    public boolean running=true;
    
    public MeterDataValidater(long meter_id,String meter_name
//            ,long connected
    ) {
        this.meter_id = meter_id;
        this.meter_name = meter_name;
//        this.connected=connected;
//        last_correct_date=data_begin;
    }
   
    @Override
    public void run() {
        Connection con=null;
        Statement st=null;
       try{
//           logger.addlog4meter("начало сканирования ",meter_name);
        con=dbpool.getPoolConnection();
//        List<Long> dates=getDates(con);
        data_begin=getLastCorrectDate(con);
//           logger.addlog4meter("data_begin="+data_begin,meter_name);
//           logger.addlog4meter("System.currentTimeMillis()="+System.currentTimeMillis(),meter_name);
//           logger.addlog4meter("180*24*60*60*1000="+(long)180*24*60*60*1000,meter_name);
//           logger.addlog4meter("System.currentTimeMillis()-180*24*60*60*1000="+(System.currentTimeMillis()-(long)180*24*60*60*1000),meter_name);
//        if (data_begin<connected)data_begin=connected;
        if (data_begin<(System.currentTimeMillis()-(long)127*24*60*60*1000)){
                data_begin=System.currentTimeMillis()-(long)127*24*60*60*1000;
                
        }
        last_correct_date=data_begin;
//        logger.addlog("проверка "+meter_name+" с периода "+data_begin);
//        loadUspd(con);
        //Collections.sort(dates);
        st=con.createStatement();
//        logger.addlog4debug("meterDataValidater meter_name="+meter_name, "befor sql execute"+"select dt from aiis.iik_pakets where (dt>"+data_begin+") and (dt<"+(System.currentTimeMillis()-3*60*60*1000)+") and (iik_id="+meter_id+") order by dt limit 0,100");
//           logger.addlog4meter("select dt from aiis.iik_pakets where (dt>"+data_begin+") and (dt<"+(System.currentTimeMillis()-(long)2*60*60*1000)+") and (iik_id="+meter_id+") order by dt "/*limit 0,5000"*/,meter_name);
        ResultSet rst=st.executeQuery("select dt from aiisdatavalidator.iik_pakets where (dt>"+data_begin+") and (dt<"+(System.currentTimeMillis()-(long)2*60*60*1000)+") and (iik_id="+meter_id+") order by dt" /*limit 0,5000"*/);
        while (rst.next()){
//        logger.addlog("проверка "+meter_name+" период c "+data_begin+" по "+rst.getLong(1));
//            if ((rst.getLong(1)-data_begin)>(24*60*60*1000)){
//           logger.addlog4meter("разница "+(rst.getLong(1)-data_begin),meter_name);
            if ((rst.getLong(1)-data_begin)>(30*60*1000*1.5)){
//           logger.addlog4meter("добавление дыры ",meter_name);
                   //sendRequest4(data_begin/1000+1,date2/1000-1,con);
                   dataHoles.add(new DataHole(data_begin+1000, rst.getLong(1)-1000,meter_id,0,0));
               }               
               data_begin=rst.getLong(1);
//               if (req_count==0)
            last_correct_date=rst.getLong(1);
           }
//        logger.addlog4debug("meterDataValidater meter_name="+meter_name, "after sql fetch");
        st.close();
        
//        for (Long date2 : dates) {
//        logger.addlog("проверка "+meter_name+" период c "+data_begin+" по "+date2);
//            if ((date2-data_begin)>(30*60*1000+1)){
//                   //sendRequest4(data_begin/1000+1,date2/1000-1,con);
//                   dataHoles.add(new DataHole(data_begin+1, date2-1,meter_id,0,0));
//               }               
//               data_begin=date2;
////               if (req_count==0)
//            last_correct_date=date2;
//           }
        for (DataHole dataHole : dataHoles) {
                  dataHole.insertIfNeed(con); 
               }
        dataHoles.clear();
//        sleep(10*60*1000);
        loadDataHoles(con);
        for (DataHole dataHole : dataHoles) {
            if (dataHole.data_count>0){
                    dataHole.destroy(con,64);
                    if (last_correct_date>dataHole.from)
                        last_correct_date=dataHole.from;}
            else{
                //организовать отсылку комманд к успд и к ИИК
//                if (){}
                //если УСПД активен -запрос на УСПД
                //если на успд (уже был запрос) нет данных -запрос к ИИК
                if (dataHole.to<((long)System.currentTimeMillis()-(long)127*24*60*60*1000)){
                    dataHole.destroy(con,128);
                }
            }
         }
        saveLastCorrectDate(con);
        
        //добавить проверку дыр!!!!
//        long to=(System.currentTimeMillis()-2*30*24*60*60*1000);
//        RemoveHoles(to, con);
//        moveData2archive(to, con);
        
        con.close();
       }catch(Exception e){
           logger.addlog("MeterDataValidater run "+e.toString());}
    try{st.close();}catch(Exception e1){}
    try{con.close();}catch(Exception e1){}
     st=null;
     con=null;
//           logger.addlog4meter("окончание сканирования",meter_name);
running=false;
    }
    
//    private List<Long>getDates(Connection con){
//        List<Long> res=new ArrayList<Long>();
//       try{ 
//        Statement st=con.createStatement();
//        ResultSet rst=st.executeQuery("select dt from aiis.iik_pakets where (dt>"+data_begin+") and (dt<"+(System.currentTimeMillis()-3*60*60*1000)+") and (iik_id="+meter_name+") order by dt limit 0,100");
//        while (rst.next())res.add(rst.getLong(1));
//        st.close();
//        logger.addlog("загружено дат "+res.size()+" "+"select dt from aiis.iik_pakets where (dt>"+data_begin+") and (dt<"+(System.currentTimeMillis()-3*60*60*1000)+") and (iik_id="+meter_name+") order by dt limit 0,100");
//       }catch(Exception e){logger.addlog("MeterDataValidater getDates "+e.toString());}
//    return res;}
//   
//    private void sendRequest4(long from,long to,Connection con){
//        req_count++;
//        for (Long uspd_id : owner_uspd) {
//            try{
//                Statement st=con.createStatement();
//                st.executeUpdate("insert into aiis.command2uspd (uspd_id,command,sender,ttl,timesend,commandadd)values("
//                    +uspd_id+",'"
//                    +"CNT_DATA_REQ','"
//                    +"-2','"
//                    +1000+"',"
//                    +System.currentTimeMillis()+",'"
//                    +"[CMD_DATA]\n\rCNT_NUM="+meter_name+"\n\rBEGIN_BATE="+from+"\n\rEND_DATE="+to+"\n\r');");
//                st.close();
//                }catch(Exception e){logger.addlog("MeterDataValidater sendRequest4 "+e.toString());}
//        }
//        }
// private void loadUspd(Connection con){
//     owner_uspd=new Vector<Long>();
//     String query="select uspd_id from aiis.metersonnet mon "
//                + " left join aiis.uspdonnet uon on (uon.net_id=mon.net_id) and (uon.disconnect is null)"
//                + " where (mon.disconnect is null) and (mon.meter_id="+meter_id+")";
//     try{ 
//        Statement st=con.createStatement();
//        ResultSet rst=st.executeQuery(query);
//        while (rst.next())owner_uspd.add(rst.getLong(1));
//        st.close();
//       }catch(Exception e){logger.addlog("MeterDataValidater loadUspd "+e.toString()+" line:"+e.getStackTrace()[0].getLineNumber()+" query"+query);}
// }      
 private void loadDataHoles(Connection con){
//     logger.addlog4meter("loadDataHoles start", meter_name);
//     owner_uspd=new Vector<Long>();
     String query="select dh.begin,dh.end,dh.state"
             + ",(select COUNT(p.id) cp from aiisdatavalidator.iik_pakets p where (p.iik_id="+meter_id+") AND (p.dt>dh.begin) AND (p.dt<dh.end) ) cp "
//             + ",count(p.id) "
             + "from aiisdatavalidator.iik_data_hole dh"
//                + " left join aiis.iik_pakets p on (p.iik_id="+meter_id+") and (p.dt>dh.begin) and (p.dt<dh.end) "
                + " where (dh.destroy is null) and (dh.iik_id="+meter_id+")"
//             + " group by dh.begin,dh.end,dh.state"
             ;
//     String query="select dh.begin,dh.end,dh.state,count(p.id) cp "
////             + ",count(p.id) "
//             + "from aiis.iik_data_hole dh"
//                + " left join aiis.iik_pakets p on (p.iik_id="+meter_id+") and (p.dt>dh.begin) and (p.dt<dh.end) "
//                + " where (dh.destroy is null) and (dh.iik_id="+meter_id+")"
//             + " group by dh.begin,dh.end,dh.state"
//             ;
//     String query1="";
     try{ 
        Statement st=con.createStatement();
//        Statement st1=con.createStatement();
//        logger.addlog4debug("meterDataValidater meter_name="+meter_name, "befor sql execute"+query);
        ResultSet rst=st.executeQuery(query);
//        logger.addlog4debug("meterDataValidater meter_name="+meter_name, "after sql execute");
        while (rst.next())
//        try{
//            query1="select count(id) from aiis.iik_pakets where (iik_id="+meter_id+") and (dt>"+rst.getLong(1)+") and (dt<"+rst.getLong(2)+")";
//
////            Statement st1=con.createStatement();
////            logger.addlog4debug("meterDataValidater meter_name="+meter_name, "befor sql_ execute"+query1);
//            ResultSet rst1=st1.executeQuery(query1);
////            logger.addlog4debug("meterDataValidater meter_name="+meter_name, "after sql_ execute");            
//            if (rst1.next()){dataHoles.add(new DataHole( rst.getLong(1), rst.getLong(2),meter_id,rst.getInt(3),rst1.getInt(1)));
            if (rst.getInt(4)>0){
//                logger.addlog4meter("found hole 4 "+meter_id,meter_name);
                dataHoles.add(new DataHole(rst.getLong(1), rst.getLong(2),meter_id,rst.getInt(3),rst.getInt(4)));
////                if (rst1.getInt(1)>0)logger.addlog("found hole >0");
            }
//        }catch(Exception e0){logger.addlog("MeterDataValidater loadDataHoles subrequest"+e0.toString()+" line:"+e0.getStackTrace()[0].getLineNumber()+" query"+query1);}
//        logger.addlog4debug("meterDataValidater meter_name="+meter_name, "after sql fetch");
//        st1.close();
        st.close();
       }catch(Exception e){logger.addlog("MeterDataValidater loadDataHoles "+e.toString()+" line:"+e.getStackTrace()[0].getLineNumber()+" query"+query);}
//      logger.addlog4meter("loadDataHoles stop", meter_name);
}      
 private void saveLastCorrectDate(Connection con){
      try{ 
//        logger.addlog("update aiis.iik_state set last_correct_date="+last_correct_date+" where iik_id="+meter_id);  
        Statement st=con.createStatement();
        st.executeUpdate("update aiisdatavalidator.iik_state set last_correct_date="+last_correct_date+" where iik_id="+meter_id);
        st.close();
       }catch(Exception e){logger.addlog("MeterDataValidater saveLastCorrectDate "+e.toString());}
 }      
 private long getLastCorrectDate(Connection con){
//     String 
     long res=0;
      try{ 
//        logger.addlog("update aiis.iik_state set last_correct_date="+last_correct_date+" where iik_id="+meter_id);  
        Statement st=con.createStatement();
        ResultSet rst=st.executeQuery("select last_correct_date from aiisdatavalidator.iik_state where iik_id="+meter_id);
        if (rst.next())res=rst.getLong(1);
        st.close();
       }catch(Exception e){logger.addlog("MeterDataValidater getLastCorrectDate "+e.toString());}
 return res;}         
//private void RemoveHoles(long to,Connection con){
//     try{ 
////        logger.addlog("update aiis.iik_state set last_correct_date="+last_correct_date+" where iik_id="+meter_id);  
//        Statement st=con.createStatement();
//        st.executeUpdate("delete from command2uspd where aiis.hole_id in (select id from aiis.iik_data_hole where iik_id="+meter_id+" and end<"+to+")");
//        st.close();
//        st=con.createStatement();
//        st.executeUpdate("delete from command2uspdarchive where hole_id in (select id from iik_data_hole where iik_id="+meter_id+" and end<"+to+")");
//        st.close();
//        st=con.createStatement();
//        st.executeUpdate("delete from iik_data_hole where iik_id="+meter_id+" and end<"+to);
//        st.close();
//       }catch(Exception e){logger.addlog("MeterDataValidater moveHoles2archive "+e.toString()+" line:"+e.getStackTrace()[0].getLineNumber()+" "+"delete from iik_data_hole where iik_id="+meter_id+" and end<"+to);}
//}
//private void moveData2archive(long to,Connection con){
//     try{ 
////        logger.addlog("update aiis.iik_state set last_correct_date="+last_correct_date+" where iik_id="+meter_id);  
//        Statement st=con.createStatement();
//        st.executeUpdate("insert into iik_data_hole () iik_data_hole where iik_id="+meter_id+" and end<"+to);
//        st.close();
//       }catch(Exception e){logger.addlog("MeterDataValidater moveHoles2archive "+e.toString());}
//}

}
