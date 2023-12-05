package ru.spb.snt.aiis.DataValidater;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import org.apache.commons.dbcp.BasicDataSource;


public class dbpool {
static Context ctx=null;//new InitialContext();
static DataSource ds=null;//(DataSource)ctx.lookup("java:comp/env/jdbc/gps");
public static void init(ConfigINI configINI){
logger.addlog("init DB");
        try{
                ctx=new InitialContext();
            logger.addlog((ds==null?"ds==null":"ds!=null"));
                if(ctx.lookup("java:comp/env/jdbc/aiis4validator")==null)logger.addlog("lookupisnull");
                else
            ds=(DataSource)ctx.lookup("java:comp/env/jdbc/aiis4validator");
            logger.addlog((ds==null?"ds==null":"ds!=null"));
        }catch(Exception epool){logger.addlog("trouble with pool "+epool.toString());}
            if (ds==null){
                BasicDataSource bds=new BasicDataSource();
                bds.setInitialSize(5);
                bds.setMinIdle(5);
                bds.setMaxIdle(10);
                bds.setMaxActive(-1);
                bds.setDriverClassName(configINI.getMySqlDriver());
                bds.setUsername(configINI.getMySqlUser());
                bds.setPassword(configINI.getMySqlPassword());
                bds.setUrl(configINI.getMySqlUrl());
                ds=bds;}
}
public static synchronized Connection getPoolConnection(){

    return getPoolConnection(0);}

public static Connection getPoolConnection(int level){
    Connection con=null;
 if (level>0)       logger.addlog("getPoolConnection "+level);
    if ((ds==null)||(level>10))
        try{init(new ConfigINI());
        }catch(Exception e){logger.addlog("getPoolConnection err"+e);}
    try{con=ds.getConnection();
    if (!con.isValid(10)) con.close();
    }
    catch(Exception e){logger.addlog("getPoolConnection err"+e);con=null;
    }
    if (con==null)con=getPoolConnection(level+1);
    return con;}

static public Long getMeter(String nomer,String type){
  Long res=null;
    Connection con=null;
    Statement  st=null;
    try{
        con=getPoolConnection();
        st=con.createStatement();
        String query="select id from aiisdatavalidator.meters where (deleted is null) and (nomer='"+nomer+"') and (type_id='"+type+"')";
        ResultSet rst=st.executeQuery(query);
        if (rst.next()){res=rst.getLong("id");
            }

        st.close();
        con.close();
    }catch(Exception e){
        logger.addlog("dbpool getMeter nomer="+nomer+" type="+type+" "+e+" line:"+e.getStackTrace()[0].getLineNumber());
    e.printStackTrace();
   } 
    try{st.close();}catch(Exception e1){}
    try{ con.close();}catch(Exception e1){}
    st=null;
    con=null;            
    return res;
}
}

