package fast.common.cipher;

import fast.common.logging.FastLogger;
import gherkin.lexer.Fa;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AES {
    private static final String CIPHER_TYPE ="AES/CBC/PKCS5Padding";
    private static final String INITIALISATION_VECTOR ="1122334455667788";
    private static String sKey=null;

    private static FastLogger logger = FastLogger.getLogger("AES");

    public static byte[] encode(byte[] data){return process(Cipher.ENCRYPT_MODE,data);}

    public static String encode(String clearText){
        byte[] data =clearText.getBytes();
        byte[] res =encode(data);
        StringBuilder builder=new StringBuilder();
        for (int i = 0; i < res.length; i++) {
            builder.append(String.format("%02x ",res[i]));
        }
        return builder.toString();
    }

    public static byte[] decode(byte[] data){return process(Cipher.DECRYPT_MODE,data);}

    public static String decode(String cipherText){
        String[] str =cipherText.split(" ");
        byte[] data =new byte[str.length];

        for (int i = 0; i < str.length; i++) {
            data[i] = Integer.decode("0x" + str[i]).byteValue();
        }
        return new String(decode(data));
    }

    public static boolean isCipherFormat(String text){
        String normalizedText = text.trim() + " ";
        String regex = "^([a-f0-9][a-f0-9] ){16,}$";
        Pattern pattern =Pattern.compile(regex);
        Matcher matcher =pattern.matcher(normalizedText);
        boolean b= matcher.find()&& normalizedText.length() %48==0;
        return b;
    }

    private static byte[] process(int mode,byte[] data){
        if(sKey ==null || sKey.isEmpty()){
            return new byte[0];
        }
        SecretKeySpec key =new SecretKeySpec(sKey.getBytes(),"AES");
        AlgorithmParameterSpec param =new IvParameterSpec(INITIALISATION_VECTOR.getBytes());
        try {
            Cipher cipher =Cipher.getInstance(CIPHER_TYPE);
            cipher.init(mode,key,param);
            return cipher.doFinal(data);
        } catch (Exception e) {
            logger.error("Error: "+e.getMessage());
            return new byte[0];
        }
    }

    public static void setSecretKeyFilePath(String secretKeyFilePath) throws IOException{
        if(sKey==null || sKey.isEmpty()){
            File sKeyFile =new File(secretKeyFilePath);
            if(!sKeyFile.exists())
                logger.warn("File not found with path " + secretKeyFilePath);
            if (sKeyFile.isFile()){
                try(FileReader fileReader=new FileReader(secretKeyFilePath);
                    BufferedReader bufferReader =new BufferedReader(fileReader);){
                    sKey =bufferReader.readLine();
                }
            }
        }
    }

    public static void SetSecretKey(String key){sKey= key;}
}
