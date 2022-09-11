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
            dbpool.init(new ConfigINI());
            List<Measure> mes=loadMeasures(); 
            initMetersState();
             for (Measure measure : mes) {
                 logger.addlog("measure="+(measure==null?"null":measure.name));
                 if (!ok) break;
                     new MeterDataValidater(measure.id, measure.name).start();
                 sleep(4*60*60*1000/mes.size());
//                 sleep(22*60*60*1000/mes.size());
             }
            logger.addlog("stop scaning");
            logger.addlog("remove old data");
            RemoveHoles((System.currentTimeMillis()-(long)6*30*24*60*60*1000));
            logger.addlog("remove old data ok");
             if (!ok)sleep(30*60*1000);
//             if (!ok)sleep(2*60*60*1000);
         }catch(Exception e){
             logger.addlog("Validater run "+e.toString());}
        }
    }

    private void initMetersState() {
        Connection con=null;
        Statement  st=null;
        try{
            con=dbpool.getPoolConnection();
            st=con.createStatement();
            st.execute("insert into aiisdatavalidator.iik_state (iik_id) select id from aiisdatavalidator.meters where not  id in (select iik_id from aiisdatavalidator.iik_state );");
            st.close();
            con.close();
        }catch(Exception e){logger.addlog("Validater moveHoles2archive "+e.toString()+" line:"+e.getStackTrace()[0].getLineNumber());}
        try{st.close();}catch(Exception e1){}
        try{con.close();}catch(Exception e1){}
        st=null;
        con=null;

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
        ResultSet rst=st.executeQuery("select m.id,m.nomer,iis.last_correct_date from aiisdatavalidator.meters m "
                + " left join aiisdatavalidator.iik_state iis on iis.iik_id=m.id"
                + " left join aiisdatavalidator.aiis_iik_state aiis_iis on aiis_iis.iik_id=m.id"
                + " where iis.last_correct_date < aiis_iis.last_paket_dt "
                + " and aiis_iis.last_paket_dt>"+(System.currentTimeMillis()-2*30*24*60*60*1000)
                + " and( m.deleted is null or m.deleted>"+(System.currentTimeMillis()-2*30*24*60*60*1000)+")");

        while (rst.next())res.add(new Measure(rst.getLong(1), rst.getString(2), rst.getLong(3)));
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
        logger.addlog("call aiisdatavalidator.deleteoldholes("+to+");");
        st.execute("call aiisdatavalidator.deleteoldholes("+to+");");
        st.close();
        con.close();
       }catch(Exception e){logger.addlog("Validater moveHoles2archive "+e.toString()+" line:"+e.getStackTrace()[0].getLineNumber());}
    try{st.close();}catch(Exception e1){}
    try{con.close();}catch(Exception e1){}
     st=null;
     con=null;
}
}
