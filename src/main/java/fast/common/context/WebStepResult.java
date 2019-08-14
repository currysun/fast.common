package fast.common.context;

import fast.common.logging.FastLogger;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Map;

public class WebStepResult extends StepResult implements ITableResult{
    private ArrayList<Map<String,Object>> result;
    private ArrayList<String> _list;

    static FastLogger logger= FastLogger.getLogger("WebStepResult");

    public WebStepResult(){
        setStatus(Status.Passed);
        result = new ArrayList<Map<String,Object>>();
        _list = new ArrayList<>();
    }

    public void setResult(ArrayList<Map<String,Object>> result){this.result =result;}

    public ArrayList<Map<String,Object>> getResult(){return result;}

    @Override
    public String toString() { return ""; }

    @Override
    public String getFieldValue(String field) throws Throwable {
        String fieldValue =null;
        if(result.size() >=1){
            fieldValue =result.get(0).get(field).toString();
        }
        return fieldValue;
    }

    @Override
    public ArrayList<String> getFieldValues(String field) {
        return _list;
    }

    @Override
    public String getCellValue(String rowIndex, String columnName) throws Throwable {
        int index =Integer.parseInt(rowIndex) -1;
        Map<String,Object> s= null;
        if(index < result.size()){
            s =result.get(index);
        }
        if(s==null) return null;
        if(!s.containsKey(columnName)){
            throw new Exception("Invalid column name");
        }
        Object cell =s.get(columnName);
        return cell ==null?null:cell.toString();
    }

    public void setFieldValues(ArrayList<String> values){_list.addAll(values);}
}
