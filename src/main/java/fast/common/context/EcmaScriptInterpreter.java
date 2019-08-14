package fast.common.context;

import fast.common.fix.TimeHelper;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public class EcmaScriptInterpreter {
    private  static EcmaScriptInterpreter instance;
    private ScriptEngineManager manager;
    private ScriptEngine engine;
    private Map<String,Object> params;

    public ScriptEngine getEngine(){ return engine;}

    private EcmaScriptInterpreter(){
        manager =new ScriptEngineManager();
        engine = manager.getEngineByName("nashorn");
        params =new HashMap<>();

        exposeJavaMethods();
    }

    private void exposeJavaMethods() {
        engine.put("generateTsWithNanoseconds",(Supplier<String>) TimeHelper::generateTsWithNanoseconds);
        engine.put("generateTsWithMicroseconds",(Supplier<String>) TimeHelper::generateTsWithMicroseconds);
        engine.put("generateTsWithMilliseconds",(Supplier<String>) TimeHelper::generateTsWithMilliseconds);
        engine.put("generateTsWithSeconds",(Supplier<String>) TimeHelper::generateTsWithSeconds);
        engine.put("generateTsWithDate",(Supplier<String>) TimeHelper::generateTsWithDate);
        engine.put("generateTsFormat",(BiFunction<String,String,String>) TimeHelper::generateTsFormat);
        //TODO
    }
    public static EcmaScriptInterpreter getInstance(){
        if(instance==null) {
            instance = new EcmaScriptInterpreter();
        }
        return instance;
    }
    public String interpret(String script) throws ScriptException {
        engine.put("params",params);
        return engine.eval(script).toString();
    }
    public <T> T interpretAndReturnAsIs(String script) throws ScriptException{
        engine.put("params",params);
        return (T) engine.eval(script);
    }

    public void addJavaMethods(String methodName,Object methodFunc){engine.put(methodName,methodFunc);}

    public Map<String,Object> getParams(){return params;}

    public void setParams(Map<String,Object> params){this.params=params;}
}
