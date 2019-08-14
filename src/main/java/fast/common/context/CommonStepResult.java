package fast.common.context;

import fast.common.logging.FastLogger;

import java.util.ArrayList;
import java.util.HashMap;

public class CommonStepResult extends StepResult{
    public static final String DefaultField = "Value";
    private HashMap<String,String> _values;
    private String errorMessage;
    static FastLogger logger= FastLogger.getLogger("CommonStepResult");

    public CommonStepResult(){
        setStatus(Status.Passed);
        _values =new HashMap<String,String>();
    }

    public String toString(){ return  _values.values().toString();}

    public void setErrorMessage(String msg){
        errorMessage =msg;
        setStatus(Status.Failed);
    }

    public String getErrorMessage(){ return errorMessage; }

    @Override
    public String getFieldValue(String field) throws Throwable { return _values.get(field); }

    public void setFieldValue(String value){_values.put(DefaultField,value);}
    public void setFieldValue(String field,String value){_values.put(field,value);}

    @Override
    public ArrayList<String> getFieldValues(String field) {
        ArrayList<String> retval =new ArrayList<>();
        retval.add(_values.get(field));
        return retval;
    }
}
