/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.spb.snt.aiis.DataValidater;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;
import java.util.Vector;

/**
 *
 * @author vadim
 */
public class Validater extends Thread{
boolean ok=true;

    @Override
    public void run() {
        while (ok){
         try{  
       MeterDataValidater mv=null;      
            logger.addlog("begin scaning");
//                 new MeterDataValidater(6316, "63006431 Временно").start();
            dbpool.init(new ConfigINI());
            List<Measure> mes=loadMeasures(); 
//            logger.addlog("for "+mes.size()+" counters");
             for (Measure measure : mes) {
//                 logger.addlog("Validater run 1");
                 logger.addlog("measure="+(measure==null?"null":measure.name));
                 if (!ok) break;
//                 logger.addlog("Validater run 2");
//                 Long mi=dbpool.getMeter("666", "3");
//                 logger.addlog("cheking meter "+measure.name);
//                if ((mi!=null)&&((mv ==null)||(!mv.running))){
//                 logger.addlog("Validater run 4");
//                 mv=new MeterDataValidater(mi, "eeee");
//                 logger.addlog("Validater run 5");
//                 mv.start();}
//                 logger.addlog("Validater run 6");
//                 logger.addlog("mi="+mi);
//                 if ((mi!=null)||(measure.id!=mi))
                     new MeterDataValidater(measure.id, measure.name).start();
//                 logger.addlog("Validater run 7");
                 sleep(22*60*60*1000/mes.size());
             }
            logger.addlog("stop scaning");
            logger.addlog("remove old data");
            RemoveHoles((System.currentTimeMillis()-(long)6*30*24*60*60*1000));
            logger.addlog("remove old data ok");
           if (!ok)sleep(2*60*60*1000);
         }catch(Exception e){
             logger.addlog("Validater run "+e.toString());}
        }
    }
    
    public void doStop(){
    ok=false;}
    
    private List<Measure> loadMeasures(){
      List<Measure> res=new Vector<Measure>();   
    Connection con=null;
    Statement  st=null;
    try{
    con=dbpool.getPoolConnection();
    st=con.createStatement();
//        logger.addlog4debug("Validater", "befor sql execute"+"select m.id,m.nomer,iis.last_correct_date from aiis.meters m "
//                + " left join aiis.iik_state iis on iis.iik_id=m.id");
        ResultSet rst=st.executeQuery("select m.id,m.nomer,iis.last_correct_date from aiis.meters m "
                + " left join aiis.iik_state iis on iis.iik_id=m.id"
                + " where iis.last_correct_date < iis.last_paket_dt "
                + " and iis.last_paket_dt>"+(System.currentTimeMillis()-2*30*24*60*60*1000)
                + " and( m.deleted is null or m.deleted>"+(System.currentTimeMillis()-2*30*24*60*60*1000)+")");
//        logger.addlog4debug("Validater", "after sql execute");

        while (rst.next())res.add(new Measure(rst.getLong(1), rst.getString(2), rst.getLong(3)));
//        logger.addlog4debug("Validater", "after sql fetch");
        st.close();
        con.close();
       }catch(Exception e){logger.addlog("Validater loadMeasures "+e.toString());}
    try{st.close();}catch(Exception e1){}
    try{con.close();}catch(Exception e1){}
     st=null;
     con=null;
    return res;} 
    private void RemoveHoles(long to){
    Connection con=null;
    Statement  st=null;
    try{
    con=dbpool.getPoolConnection();
    st=con.createStatement();
        logger.addlog("call aiis.deleteoldholes("+to+");");
        st.execute("call aiis.deleteoldholes("+to+");");
//        logger.addlog("delete from aiis.command2uspd where hole_id in (select id from aiis.iik_data_hole where end<"+to+")");
//        st.executeUpdate("delete from aiis.command2uspd where hole_id in (select id from aiis.iik_data_hole where end<"+to+")");
//        st.close();
//        st=con.createStatement();
//        logger.addlog("delete from aiis.command2uspdarchive where hole_id in (select id from aiis.iik_data_hole where end<"+to+")");
//        st.executeUpdate("delete from aiis.command2uspdarchive where hole_id in (select id from aiis.iik_data_hole where end<"+to+")");
//        st.close();
//        st=con.createStatement();
//        logger.addlog("delete from aiis.iik_data_hole where end<"+to);
//        st.executeUpdate("delete from aiis.iik_data_hole where end<"+to);
        st.close();
        con.close();
       }catch(Exception e){logger.addlog("Validater moveHoles2archive "+e.toString()+" line:"+e.getStackTrace()[0].getLineNumber());}
    try{st.close();}catch(Exception e1){}
    try{con.close();}catch(Exception e1){}
     st=null;
     con=null;
}
}
