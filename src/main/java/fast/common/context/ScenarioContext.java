package fast.common.context;

import fast.common.core.Configurator;
import fast.common.logging.FastLogger;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

public class ScenarioContext implements AutoCloseable{
    public final static String PARAMS_MAP_NAME="Params";
    public final static String IHREADPARAMS_MAP_NAME ="ThreadParams";
    public final static String THREAD_NUMBER_SETTING ="thread-number ";
    private static FastLogger logger =FastLogger.getLogger("ScenarioContext");
    private EvalScope  _evalScope=new EvalScope();
    //
    private int threadNumber;
    private StepResult _lastStepResult;
    private Object _scenario;
    private HashSet<String> tag37set= new HashSet<>();
    @Override
    public void close() throws Exception {
        logger.debug("Clearing Scenario Context");
        _evalScope.close();
        tag37set.clear();
    }
    public ScenarioContext(Object _scenario) throws Exception{
        LoadConfig();
        _scenario=_scenario;
    }

    private void LoadConfig() throws Exception{
        logger.debug("All System Properties");
        Properties props =System.getProperties();
        Iterator iterator = props.keySet().iterator();
        while (iterator.hasNext()){
            String propertyName = (String) iterator.next();
            String propertValue = props.getProperty(propertyName);
            logger.debug(propertyName +": "+ propertValue);
        }

        //TODO: EcmaScript
        EcmaScriptInterpreter.getInstance().setParams((Map) Configurator.getInstance().getSettingsMap().get(PARAMS_MAP_NAME));
        if(EcmaScriptInterpreter.getInstance().getParams()==null){
            logger.warn(String.format("Section '%s' is not defined in config",PARAMS_MAP_NAME));
        }
    }
    public HashSet<String> getTag37set(){return tag37set;}

    public void setTag37set(HashSet<String> tag37set){this.tag37set = tag37set;}

    public <T extends  Object> T getScenario(){
        return (T)_scenario;
    }

    public StepResult getLastResultVariable(){return _lastStepResult;}

    public <T extends StepResult> T getVariable(String varName){ return (T) _evalScope.getVar(varName);}

    public String processString(String str){
        return _evalScope.processString(str);
    }

    public void setThreadNumberSetting(String paramName,Object value) throws Exception{
        _evalScope.updateParam(paramName,value);
    }

    public void saveLastStepResult(StepResult _lastStepResult,String varName){
        _lastStepResult =_lastStepResult;
        if(varName!=null){
            _evalScope.saveVar(varName,_lastStepResult);
        }
    }

}
