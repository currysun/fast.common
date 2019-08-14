package fast.common.cipher;

import fast.common.logging.FastLogger;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

public class PrivateKeyGen {
    private static FastLogger logger=FastLogger.getLogger(PrivateKeyGen.class.getName());

    /**
     * Get private key from file
     */

    public static String getKey(String filePath){
        File file =new  File(filePath);
        if(file.canRead()){
            try{
                return Files.readAllLines(Paths.get(filePath)).get(0);
            }catch (Exception e){
                logger.error("Fail to read the key file with exception :\n"+ e.getMessage());
            }
        }
        return null;
    }
}
