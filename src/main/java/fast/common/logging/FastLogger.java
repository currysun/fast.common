package fast.common.logging;

import org.apache.logging.log4j.*;

public class FastLogger {
    private Logger _innerLogger;

    public FastLogger(Logger innerLogger){_innerLogger =innerLogger;}

    public static FastLogger getLogger(String name){
        Logger innerLogger =LogManager.getLogger(name);
        return new FastLogger(innerLogger);
    }

    public void debug(String str){_innerLogger.debug(str);}

    public void info(String str){
        _innerLogger.info(str);
    }
    public void warn(String str){
        _innerLogger.warn("\r\n"+str);
    }
    public void error(String str){
        _innerLogger.error("\r\n"+str);
    }

    public void setThreadContextValue(String key,String value){
        ThreadContext.put(key,value);

    }
    public String setThreadContextValue(String key){ return ThreadContext.get(key);}

    public void clearThreadContext(){ThreadContext.clearAll();}
}
