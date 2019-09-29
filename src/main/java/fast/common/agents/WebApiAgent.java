package fast.common.agents;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import fast.common.context.StepResult;
import fast.common.context.WebApiResult;
import fast.common.core.Configurator;
import fast.common.logging.FastLogger;
import io.restassured.RestAssured;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.preemptive;

/**
 * The {@code WebApiAgent} class defines various basic http requests to interact
 * with web server using web api.
 *
 * <p>The basic requests : GET,POST,PUT,DELETE<p/>
 *
 * <p>
 *     Details information for using on WebApiAgent can se
 * <p/>
 *
 */
public class WebApiAgent extends Agent{

    private RequestSpecification uncompletedRequest;

    /**
     * Four request types:
     *
     * GET,POST,PUT,DELETE
     *
     * @throws Exception
     */

    public enum RequestType{
        GET,POST,PUT,DELETE
    }

    private FastLogger logger;
    private static final String APPLICATION_JSON="application/json";

    /**
     * Constructs a new <tt>WebApiAgent<tt/> with
     * default configuration file (config.yml) and custom configuration files to
     * fetch required parameters.
     *
     * @param name a string for naming the creating WebApiAgent
     * @param agentParams a map to get the required parameters for creating a WebApiAgent
     * @param configurator a Configurator instance to provide confiruation info for the actions of the WebApiAgent
     *
     * @throws Exception
     */
    public WebApiAgent(String name, Map<?,?> agentParams, Configurator configurator){
        super(name,agentParams,configurator);
        logger = FastLogger.getLogger(String.format("%s:WebApiAgent",_name));
    }

    /**
     *
     * Contructs a new <tt>WebApiAgent<tt/> with no parameter.
     * Diplay a specified log information
     * @throws Exception
     */
    public WebApiAgent(){
        super();
        logger = FastLogger.getLogger("WebApiAgent");
        logger.info("Initializing Web Api Agent....");
        RestAssured.replaceFiltersWith(ResponseLoggingFilter.responseLogger(),new ResponseLoggingFilter());
    }

    /**
     * Contructs a new <tt>WebApiAgent<tt/> with no parameter.
     * @param uri address used to send request
     * @throws Exception
     *
     */
    public WebApiAgent(String uri){
        this();
        RestAssured.baseURI=uri;
    }
    /**
     * Contructs a new <tt>WebApiAgent<tt/> with username and password
     * @param username used to verify
     * @param password used to verify
     * @throws Exception
     *
     */
    public WebApiAgent(String username,String password){
        this();
        RestAssured.authentication=preemptive().basic(username,password);
        this.useRelaxedHTTPSValidation();
    }

    /**
     * Contructs a new <tt>WebApiAgent<tt/> with uri username and password
     *
     * @param uri address used to send request
     * @param username used to verify
     * @param password used to verify
     * @throws Exception
     *
     */
    public WebApiAgent(String uri,String username,String password){
        this(username,password);
        RestAssured.baseURI=uri;

    }

    public void useRelaxedHTTPSValidation(){
        RestAssured.useRelaxedHTTPSValidation();
    }

    /**
     * Create a WebApiAgent building the reuqest part
     * @throws Exception
     */
    public WebApiAgent generate(){
        if(uncompletedRequest==null){
            uncompletedRequest=given();
        }
        return this;
    }

    /**
     * Set content type to a send request.
     *
     * @param contentType define request type
     * @return a request a specification with content type
     * @throws Exception
     */

    public WebApiAgent setContentType(String contentType){
        generate();
        uncompletedRequest=uncompletedRequest.contentType(contentType);
        return this;
    }

    /**
     * Set additional /custom headers to send in the request
     * @param headers define additional /custom headers user want to send, apart from default headers.
     * @return a request a specification with headers
     * @throws Exception
     */
    public WebApiAgent setHeaders(Map<String,String> headers){
        generate();
        uncompletedRequest= uncompletedRequest.headers(headers);
        return this;
    }
    /**
     * Set path params to a send request uri
     * @param pathParams define request uri path params
     * @return a request a specification with pathParams
     * @throws Exception
     */
    public WebApiAgent setPathParams(Map<String,Object> pathParams){
        generate();
        uncompletedRequest= uncompletedRequest.pathParams(pathParams);
        return this;
    }
    /**
     * Set body to a send request
     * @param body define request body
     * @return a request a specification with body
     * @throws Exception
     */
    public WebApiAgent setBody(Object body){
        generate();
        uncompletedRequest= uncompletedRequest.body(body);
        return this;
    }

    /**
     * Set  params type to a send request
     * @param params define request  params
     * @return a request a specification with Params
     * @throws Exception
     */
    public WebApiAgent setParams(Map<String,String> params){
        generate();
        uncompletedRequest= uncompletedRequest.params(params);
        return this;
    }
    /**
     * Set cookies to a send request
     * @param cookies define request cookies
     * @return a request a specification with cookies
     * @throws Exception
     */
    public WebApiAgent setCookies(Map<String,String> cookies){
        generate();
        uncompletedRequest= uncompletedRequest.params(cookies);
        return this;
    }

    /**
     * Set request and get response to store in StepResult
     *
     * @param type including "GET POST PUT DELETE"
     * @uri uri define request address
     * @return result store response into stepResult
     * @throws Exception
     * @see fast.common.glue.CommonStepDefs#sendRequest(String,String,String,String)
     */
    public WebApiResult sendRequest(RequestType type, String uri){
        generate();
        WebApiResult result=null;
        try{
            Response response=null;
            switch (type){
                case GET:
                    response = uncompletedRequest.get(uri);
                    break;
                case POST:
                    response = uncompletedRequest.post(uri);
                    break;
                case PUT:
                    response = uncompletedRequest.put(uri);
                    break;
                case DELETE:
                    response = uncompletedRequest.delete(uri);
                    break;
            }
            result= new WebApiResult(response);
            result.setStatus(StepResult.Status.Passed);
        }catch (Exception ex){
            result= new WebApiResult();
            result.setStatus(StepResult.Status.Failed);
            result.setFailedMessage(ex.getMessage());
        }finally {
            uncompletedRequest =null;
        }
        return result;
    }

    /**
     * Send rest request and get response to store in StepResult.
     *
     * @param type including "GET POST PUT DELETE"
     * @param uri define request address
     * @param pathParams define uri address path params
     * @param params define request params
     * @param body define request body
     * @return result store response into stepResult
     *
     * @see fast.common.glue.CommonStepDefs#sendRestRequest(String,String,String,String,String,String,String)
     */

    public StepResult sendRestRequest(RequestType type,String uri,Map<String,Object> pathParams,Map<String,Object> params,Map<String,Object> body){
        this.generate().setContentType(APPLICATION_JSON);
        if(pathParams !=null){
            this.setPathParams(pathParams);
        }
        if(params!=null){
            this.setPathParams(params);
        }
        if(body !=null){
            this.setBody(body);
        }
        return this.sendRequest(type,uri);
    }

    /**
     * Sends http request to get responses from web server
     * @param requestType
     *          GET POST PUT DELETE
     * @param uri
     *          the address of web server to send request
     * @param jsonObject
     *          the request body provides with fields and values
     * @return the WebApiResult stores contains status code and response
     *
     * <pre>
     *     WebApiResult response = new WebApiAgent().sendRestRequest(RequestType.GET,uri,null);
     * </pre>
     */


    @Deprecated
    public WebApiResult sendRestRequest(RequestType requestType ,String uri,JSONObject jsonObject){
        logger.info(String.format("Request URI: %s. Request Type: %s",uri,requestType));
        Map<String, Object> processedJsonObject =null;
        if(jsonObject !=null){
            processedJsonObject = JSONObject.parseObject(jsonObject.toJSONString(),new TypeReference<Map<String,Object>>(){});
        }
        return (WebApiResult) this.sendRestRequest(requestType,uri,null,null,processedJsonObject);
    }




    @Override
    public void close() throws Exception {
        logger.info("WebApi Agent closes");
    }
}
