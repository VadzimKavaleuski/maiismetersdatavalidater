package ru.spb.snt.aiis.DataValidater;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.DriverManagerConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingConnection;
import org.apache.commons.dbcp.PoolingDataSource;
import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.impl.GenericObjectPool;



/**
 *
 * @author Администратор
 */
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
            //    bds.setMaxWait(60*1000);
                bds.setDriverClassName(configINI.getMySqlDriver());
                bds.setUsername(configINI.getMySqlUser());
                bds.setPassword(configINI.getMySqlPassword());
                bds.setUrl(configINI.getMySqlUrl());
            //    bds.setAccessToUnderlyingConnectionAllowed(true);
            //    bds.setTestOnReturn(true);
            //    bds.setTestOnBorrow(true);
            //    bds.setTestWhileIdle(true);

            //    logger.addlog("ValidationQuery"+bds.getValidationQuery());
            //    logger.addlog("getValidationQueryTimeout"+bds.getValidationQueryTimeout());
                ds=bds;}
}
public static synchronized Connection getPoolConnection(){
//        logger.addlog("getPoolConnection ");
    
    return getPoolConnection(0);}

public static Connection getPoolConnection(int level){
    Connection con=null;
//    if (level>0)
 if (level>0)       logger.addlog("getPoolConnection "+level);
    if ((ds==null)||(level>10))
        try{init(new ConfigINI());
//            ctx=new InitialContext();
//            ds=(DataSource)ctx.lookup("java:comp/env/jdbc/aiisWebServer");
//            if (con==null)init(new ConfigINI());
        }catch(Exception e){logger.addlog("getPoolConnection err"+e);}
    try{con=ds.getConnection();
    if (!con.isValid(10)) con.close();
    }
    catch(Exception e){logger.addlog("getPoolConnection err"+e);con=null;
//        init(registrator.registrator.configINI);
    }
//    logger.addlog("poll active"+ds.getNumActive()+" idle "+ds.getNumIdle());
    if (con==null)con=getPoolConnection(level+1);
    return con;}

static public Long getMeter(String nomer,String type){
  Long res=null;
//  logger.addlog("getORinsertMeter_id nomer="+nomer+" type="+type+" ip="+ip);
//  logger.addlog("getORinsertMeter_id "+ip);
    Connection con=null;
    Statement  st=null;
    try{
        con=getPoolConnection();
        st=con.createStatement();
//        logger.addlog4debug("dbpool", "befor sql execute"+"select id,nomer,type_id,year from aiis.meters where (deleted is null) and (ip='"+ip+"')");
//        long start=System.currentTimeMillis();
        String query="select id from aiis.meters where (deleted is null) and (nomer='"+nomer+"') and (type_id='"+type+"')";
//        String query=(!type.equals("0"))?"select id,nomer,type_id,year from aiis.meters where (deleted is null) and (nomer='"+nomer+"') and (type_id='"+type+"')":"select id,nomer,type_id,year from aiis.meters where (deleted is null) and (ip='"+ip+"')";
        ResultSet rst=st.executeQuery(query);
//        logger.addquerylog(start, System.currentTimeMillis(), query);
//        logger.addlog4debug("dbpool", "after sql execute");


        if (rst.next()){res=rst.getLong("id");
            }

        st.close();
        con.close();
    }catch(Exception e){
        logger.addlog("dbpool getMeter nomer="+nomer+" type="+type+" "+e+" line:"+e.getStackTrace()[0].getLineNumber());
//        dbpool.addSysEvent(3, "PushSqls pushsqls Ошибка исполнения SQL: "+e);
    e.printStackTrace();
   } 
    try{st.close();}catch(Exception e1){}
    try{ con.close();}catch(Exception e1){}
    st=null;
    con=null;            
    return res;
}
}

