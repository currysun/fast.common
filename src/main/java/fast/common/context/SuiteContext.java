package fast.common.context;

import fast.common.core.Configurator;
import fast.common.logging.FastLogger;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class SuiteContext {

    private static SuiteContext instance=null;
    private static Lock lock =new ReentrantLock();
    private static FastLogger logger= FastLogger.getLogger("SuiteContext");
    public static final String CONFIG_SUITE_HOOK ="suiteHook";

    private SuiteContext(){init();}

    public static SuiteContext getInstance(){
        try{
            lock.lock();
            if (instance ==null){
                instance =new SuiteContext();
            }
        }finally {
            lock.unlock();
        }
        return instance;
    }
    private void init(){
        Object suiteHook =null;
        try{
            suiteHook = Configurator.getInstance().getSettingsMap().get(CONFIG_SUITE_HOOK);
        }catch (Exception ex){
            logger.info("No Suite Hook is configured");
        }
        if (suiteHook !=null){
            logger.info(String.format("Start to call Suite Hook: %s",suiteHook));
            try {
                String className =suiteHook.toString();
                ISuiteHook hook =(ISuiteHook)Class.forName(className).newInstance();
                hook.beforeSuite();
            }
            catch (Exception ex){
                logger.error("Error happend in BeforeSuite,skipp all test scenarios and exit test suite");
                System.exit(1);
            }
        }
    }
    public void verifyFrameworkVersion(){
        //to call REST service to check if the framework being used in the latest or not
    }
}
