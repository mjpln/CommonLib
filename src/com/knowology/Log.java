package com.knowology;


import org.apache.log4j.Logger;   
import org.apache.log4j.PropertyConfigurator;   

public class Log {
	 //Logger实例   
    private Logger loger;   
    //将Log类封装成单实例的模式，独立于其他类。以后要用到日志的地方只要获得Log的实例就可以方便使用   
    private static Log log;   
    public static String lplatform=System.getProperty("os.name");
    //构造函数，用于初始化Logger配置需要的属性   
    private Log()   
    {   
        //获得当前目录路径   
        String filePath=this.getClass().getResource("/").getPath();   
        filePath = filePath.replace("%20", " ");   //路径中的空格会被误读成“%20”
        //找到log4j.properties配置文件所在的目录(已经创建好)   
        filePath=filePath.substring(1).replace("bin", "src");   
        //获得日志类loger的实例   
        loger=Logger.getLogger(this.getClass());   
        //loger所需的配置文件路径   
        if(!filePath.startsWith("/")&&lplatform.contains("Linux"))filePath="/"+filePath;
        PropertyConfigurator.configure(filePath+"log4j.properties");         
    }   
       
   public static Log getLoger()   
    {   
        if(log!=null)   
            return log;   
        else  
            return new Log();   
    }   
    
    public void error(String message)
    {
    	if(GlobalValue.IsDebug)
    	{
    		System.out.println("『ERROR』"+message);
    	}
    	loger.error(message);
    }
    
    public void info(String message)
    {
    	loger.info(message);
    }

    public void debug(String message)
    {
    	loger.debug(message);
    }

	public void fatal(String message) {
		// TODO Auto-generated method stub
		loger.fatal(message);
	}

	public void warn(String message) {
		// TODO Auto-generated method stub
		loger.warn(message);
	}
    
    //例子
//	Log myLog=Log.getLoger();
//	myLog.error("try to send error message");
//	myLog.debug("try to send debug message");
//	myLog.info("try to send info message");
}
