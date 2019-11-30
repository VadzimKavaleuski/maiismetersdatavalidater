package ru.spb.snt.aiis.DataValidater;
import java.io.File;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalINIConfiguration;
public class ConfigINI extends HierarchicalINIConfiguration {

    public ConfigINI() throws ConfigurationException {
        super(new File("conf/aiis-datavalidater"));
        setAutoSave(true);
//        logger.addlog("ConfigINI 1");
//                for (Iterator<String> is =this.getKeys(); is.hasNext();) {
//            String object = is.next();
//             logger.addlog(object);
//        }
//         logger.addlog("ConfigINI 2");
//        SetDataInit(System.currentTimeMillis());
SetDataInit(-1);
    }
public String getMySqlUser(){
//    logger.addlog("getMySqlUser "+getSection("MySql").getString("User"));        
    return getSection("MySql").getString("User","aiisroot");}
public void SetMySqlUser(int val){
    getSection("MySql").setProperty("User", val);
    try {
        this.save();// NodeList("CarPosition");
    } catch (Exception e) {logger.addlog("ConfigINI ошибка записи: "+e);}
}
public String getMySqlPassword(){
//    logger.addlog("getMySqlPassword "+getSection("MySql").getString("Password"));  
    return getSection("MySql").getString("Password","aiisroot");}
public void SetMySqlPassword(int val){
    getSection("MySql").setProperty("Password", val);
    try {
        this.save();// NodeList("CarPosition");
    } catch (Exception e) {logger.addlog("ConfigINI ошибка записи: "+e);}
}
public String getMySqlDriver(){
    return getSection("MySql").getString("Driver","com.mysql.jdbc.Driver");}
public String getMySqlUrl(){
    return getSection("MySql").getString("Url","jdbc:mysql://127.0.0.1/aiis");}
public int getPort(){
    return getSection("main").getInt("Port");}
public void SetDataInit(long val){
    try {
        logger.addlog("TimeInit="+val);
        getSection("Test").setProperty("TimeInit", val);
    
//        this.save();// NodeList("CarPosition");
    } catch (Exception e) {logger.addlog("ConfigINI write TimeInit error: "+e);}
}


}
