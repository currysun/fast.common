package fast.common.context;


import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fast.common.logging.FastLogger;

import java.io.IOException;
import java.util.Map;

public class HelperMethods {
    static FastLogger logger = FastLogger.getLogger("HelpMethods");

    /**
     * Process json string to Map<String,Object> Object
     * @param str the json string
     * @return a Map <String,Object> Object
     */
    public static Map<String,Object> processStringToMap(String str){
        ObjectMapper objectMapper =new ObjectMapper();
        objectMapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES,true);
        Map<String,Object> mapObject =null;
        try{
            mapObject =objectMapper.readValue(str,Map.class);
        }catch (JsonParseException e){
            logger.warn("JsonParseException: " + e.getMessage());
        }catch (JsonMappingException e){
            logger.warn("JsonMappingException: " + e.getMessage());
        }catch (IOException e){
            logger.warn("IOException: " + e.getMessage());

        }
    return mapObject;
    }
}
