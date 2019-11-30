package ru.spb.snt.aiis.DataValidater;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class DataHole {
 long from;
 long to;
 long iik_id;
 int state;
 int data_count;

    public DataHole(long from, long to, long iik_id, int state,int data_count) {
        this.from = from;
        this.to = to;
        this.iik_id = iik_id;
        this.state = state;
        this.data_count=data_count;
    }

    public void insertIfNeed(Connection con){
        try {
            Statement st=con.createStatement();
//            logger.addlog4debug("DataHole iik_id"+iik_id,"befor sql execute"+"select count(*) from aiis.iik_data_hole dh "
//                    + " where (destroy is null)"
//                    + " and (iik_id="+iik_id+")"
//                    + " and (dh.begin<="+from+") and (dh.end>="+to+")");
            ResultSet rst=st.executeQuery("select count(*) from aiis.iik_data_hole dh "
                    + " where "
                        + "("
        //                    + "(destroy is null) or "
                            + "(state!=64)"
                        + ")"
                        + " and "
                        + "(iik_id="+iik_id+")"
                        + " and (dh.begin<="+from+") and (dh.end>="+to+")");
//            logger.addlog4debug("DataHole iik_id"+iik_id, "after sql execute");
            int c=0;
            if (rst.next())c=rst.getInt(1);
            rst.close();
            if (c==0){
             st.executeUpdate("insert into aiis.iik_data_hole (iik_id,begin,end,state)"
                     + " values ("+iik_id+","+from+","+to+","+state+")");
            }
//            logger.addlog4debug("DataHole iik_id"+iik_id, "after sql fetch");
            st.close();
        } catch (Exception e) {logger.addlog("DataHole insertIfNeed err "+e.toString() +"query "+"insert into aiis.iik_data_hole (iik_id,begin,end,state)"
                     + " values ("+iik_id+","+from+","+to+","+state+")");
        }
    }
    private void destroy(Connection con){
        try {
//            logger.addlog("destroy4 "+iik_id);
            Statement st=con.createStatement();
            String query="delete from aiis.iik_data_hole "
//                    + " set "
//                    + " destroy="+System.currentTimeMillis()+","
//                    + " state="+state
                     + " where (iik_id="+iik_id+") and (begin="+from+")and(end="+to+")";
//                     + " where (destroy is null)and(iik_id="+iik_id+") and (begin="+from+")and(end="+to+")";
            st.executeUpdate(query);
            st.close();
//            st.executeUpdate("update aiis.iik_data_hole set "
//                    + " destroy="+System.currentTimeMillis()+","
//                    + " state="+state
//                     + " where (destroy is null)and(iik_id="+iik_id+") and (begin="+from+")and(end="+to+")");
//            st.close();
        } catch (Exception e) {logger.addlog("DataHole destroy err "+e.toString());
        }
    }
public void destroy(Connection con,int status){
    state=status;
    destroy(con);
   }
}
