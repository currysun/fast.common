package fast.common.context;

import fast.common.logging.FastLogger;
import io.restassured.http.Header;
import io.restassured.response.Response;
import io.restassured.response.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.path.xml.XmlPath.from;

public class WebApiResult extends StepResult{
    private  int statusCode=0;
    private String statusLine="default";

    private String contentType="default";
    HashMap<String,String> headerMap =new HashMap<>();
    private String responseBodyContent="default";
    private ResponseBody responseBody;
    private Map<String,String> cookies;
    private long responseTime=0;

    static FastLogger logger = FastLogger.getLogger("WebApiResult");

    enum DataType{
        isString,isInt,isBoolean,isByte,isChar,isDouble,isFloat,isLong,isShort;
    }

    public WebApiResult(){

    }
    public WebApiResult(int statusCode,String responseBodyContent){
        this.statusCode =statusCode;
        this.responseBodyContent =responseBodyContent;
    }

    public WebApiResult(Response response){
        //Extrafting status line
        this.statusCode =response.getStatusCode();
        this.statusLine =response.getStatusLine();

        //Extrafting Headers
        this.contentType =response.getContentType();

        //Extracting headers
        List<Header> headers =response.headers().asList();
        for (Header header: headers){
            this.headerMap.put(header.getName(),header.getValue());
        }

        //Extract Time
        this.responseTime=response.getTime();

        //Extract Cookies
        this.cookies=response.getCookies();

        //Extract Body
        this.responseBody=response.getBody();
        this.responseBodyContent=response.asString();
    }

    /**
     * get cookie value by key
     *
     * @prarm key
     *
     * @return cookie value for key
     */
    public String getCookieValue(String key){
        if(cookies.containsKey(key)){
            return cookies.get(key);
        }
        return null;
    }

    /**
     * get response status code
     *
     * @return response statusCode
     */
    public int getStatusCode(){ return statusCode;}

    /**
     * get response body json as string
     * @return response body string
     */
    public String getJsonResult(){
        if(responseBodyContent ==null && responseBody!=null){
            responseBodyContent =responseBody.asString();
        }
        return responseBodyContent;
    }

    /**
     * get json Obkectfrom jsonArray
     *
     * @param index of json Object array
     * @return json Object
     */

    public Object getArrayitem(int index){
        String bodyStr;
        if(responseBody==null)
        {
            bodyStr=responseBodyContent;
        }else{
          bodyStr =responseBody.asString();
        }
        List<Object> items =from(bodyStr).getList("$");
        return items.get(index);
    }

    /**
     * get field value as objectfrom json string.
     * @param jsonString source string
     *
     * @param path target json object string
     *
     * @return json Object
     */
    public Object getFieldValueFromJsonString(String jsonString,String path){return from(jsonString).get(path);}

    @Override
    public String toString(){ return this.getJsonResult();}


    @Override
    public String getFieldValue(String field) throws Throwable {
        if(this.responseBody==null){
            return this.responseBody.path(field).toString();
        }else if (this.responseBodyContent !=null && !this.responseBodyContent.isEmpty()){
            return from(responseBodyContent).get(field).toString();
        }else{
            return null;
        }
    }

    /**
     * get Map object field value from string response
     *
     * @param jsonPath the target json path wanted to query
     * @return Map object foeld value
     */
    public Map<?,?> getFieldValueAsMapFromStringResponse(String jsonPath){
        if(this.responseBody !=null){
            Map<?,?> jsonMap =this.responseBody.jsonPath().getMap(jsonPath);
            return jsonMap;
        }else{
            logger.info("responseBody is null");
            return null;
        }
    }

    /**
     * get List object field value from String response
     * @param jsonPath the target json path wanted to query
     * @returnlist object field value
     */
    public List<?> getFieldValueAsListFromStringResponse(String jsonPath){
        if(this.responseBody!=null){
           List<?> jsonList =this.responseBody.jsonPath().getList(jsonPath);
           return jsonList;
        }else{
            logger.info("responseBody is null");
            return null;
        }
    }

    /**
     * get String object field value from string response
     *
     * @param jsonPath the target json path wanted to query
     * @return String object field value
     */
    public String getFieldValueAsStringFromStringResponse(String jsonPath){
        if(this.responseBody!=null){
            String jsonString =this.responseBody.jsonPath().getString(jsonPath);
            return jsonString;
        }else{
            logger.info("responseBody is null");
            return null;
        }
    }

    @Override
    public ArrayList<String> getFieldValues(String field) {
        return null;
    }
}
