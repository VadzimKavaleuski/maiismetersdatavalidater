package ru.spb.snt.aiis.DataValidater;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import org.apache.commons.io.FileUtils;

public final class logger {
    static  SimpleDateFormat df = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    static  SimpleDateFormat dffn = new java.text.SimpleDateFormat("yyyy-MM-dd HH");
    static  byte ns[]={10,13};
 
  public synchronized static  void addlog(String s){
        String os;
        FileOutputStream ofs;
        os=df.format(System.currentTimeMillis()).toString()+" "+s;
        //fmt.close();
        try{
//            ofs=new FileOutputStream("aiis-datavalidater "+dffn.format(System.currentTimeMillis()).toString(),true);
        // String dir="logs/aiis-datavalidater";
            // FileUtils.forceMkdir(new File(dir));
            // ofs=new FileOutputStream("logs/aiis-datavalidater/"+dffn.format(System.currentTimeMillis()).toString(),true);
            ofs=out;
            ofs.write(os.getBytes());
            ofs.write(ns);
            ofs.close();
        }catch(Exception e){System.out.println("file open/create error: "+e+" "+os);}
        }
  public synchronized static  void addlog4meter(String s,String name){
        String os;
        FileOutputStream ofs;
        os=df.format(System.currentTimeMillis()).toString()+" "+s;
        //fmt.close();
        try{
//            ofs=new FileOutputStream("aiis-datavalidater "+dffn.format(System.currentTimeMillis()).toString(),true);
        String dir="logs/aiis-datavalidater/"+name;
            FileUtils.forceMkdir(new File(dir));
            ofs=new FileOutputStream("logs/aiis-datavalidater/"+name+"/"+dffn.format(System.currentTimeMillis()).toString(),true);
            ofs.write(os.getBytes());
            ofs.write(ns);
            ofs.close();
        }catch(Exception e){System.out.println("file open/create error: "+e+" "+os);}
        }

//  public synchronized static  void addlog4debug(String sender,String s){
//        String os;
//        FileOutputStream ofs;
//        try{
//            os=df.format(System.currentTimeMillis()).toString()+" "+s;
//        //fmt.close();
//        
//            ofs=new FileOutputStream("logs/debug/"+sender+" "+dffn.format(System.currentTimeMillis()).toString(),true);
//            ofs.write(os.getBytes());
//            ofs.write(ns);
//            ofs.close();
//        }catch(Exception e){System.out.println("file open/create error: "+e);}
//        }
}
