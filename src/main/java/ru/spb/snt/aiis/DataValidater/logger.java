package ru.spb.snt.aiis.DataValidater;
import java.text.SimpleDateFormat;

public final class logger {
    static  SimpleDateFormat df = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    static  SimpleDateFormat dffn = new java.text.SimpleDateFormat("yyyy-MM-dd HH");
    static  byte ns[]={10,13};
 
  public synchronized static  void addlog(String s){
        String os=df.format(System.currentTimeMillis()).toString()+" "+s;
        	System.out.println(os);
        }
  public synchronized static  void addlog4meter(String s,String name){
        String os;
        os=df.format(System.currentTimeMillis()).toString()+" meter["+name+"]>>>"+s;
        System.out.println(os);
        }

}
