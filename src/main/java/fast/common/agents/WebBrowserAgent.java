package fast.common.agents;

import com.esotericsoftware.yamlbeans.YamlException;
import com.esotericsoftware.yamlbeans.YamlReader;
import cucumber.api.java.it.Ma;
import fast.common.context.CommonStepResult;
import fast.common.context.StepResult;
import fast.common.core.Configurator;
import fast.common.logging.FastLogger;

import gherkin.formatter.model.Step;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.BrowserType;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import javax.swing.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WebBrowserAgent extends Agent {
    /**
     * The {@code WebBrowserAgent} class defines various common actions for automation tests of web UI using selenium Webdriver
     *<p>The actions in this class required Xpath or CssSelector of the element in the web page<p/>
     *<p>The basic includes : open url,click,type,check page element<p/>
     *<p>Details information for using a WebrowserAgent can see:
     * <p><a href="https://cedt-confluence..."><a/><p/>
     *
      */

    public static final String CONFIG_CHROME_PREFS= "chromePrefs";

    public static final String CONFIG_HEADLESS_BROSER= "headlessBrowser";

    public static final String CONFIG_AUTO_DOWNLOAD= "download.default_directory";

    public static final String DYNAMIC_INDEX= "dynamicIndex";

    public static final String CONFIG_DESIRED_CAPABILITIES = "desiredCapabilities";
    public static final String CONFIG_DESIRED_CAPABILITIES_BROWSER_NAME = "browserName";
    public static final String CONFIG_DESIRED_CAPABILITIES_HEADLESS = "headless";
    public static final String CONFIG_DESIRED_CAPABILITIES_PLATFORM = "platform";
    public static final String LOCATION_VARIABLE_NAME = "<LocationVariable>";

    public static final String CONFIG_REPOSITORY_NAME= "Repository";

    public static final String PARAM_DRIVER_CLASSNAME="driverClassName";
    public static final String PARAM_DRIVER_REMOTEADDRESS="driverRemoteAddress";
    public static final String PARAM_WEB_DRIVER_PATH="webDriverPath";
    public static final String PARAM_PROXY="proxy";
    public static final String PARAM_ARGUMENTS="chromeArgument";
    public static final String PARAM_APP_BINARY_PATH="appBinaryPath";

    public static final String CLASS_CHROME_DRIVER="org.openqa.selenium.chrome.ChromeDriver";
    public static final String CLASS_IE_DRIVER="org.openqa.selenium.ie.InternetExplorerDriver";

    public static final String PROPERTY_CHROME_DRIVER="webdriver.chrome.driver";
    public static final String PROPERTY_IE_DRIVER="webdriver.ie.driver";

    public static final String WEBELEMENT_TAG_TBODY="tbody";
    public static final String WEBELEMENT_TAG_TR="tr";
    public static final String WEBELEMENT_TAG_TD="td";

    private FastLogger _logger;
    private Map<?,?> repo;
    private String currentPage;

    private WebDriver _driver;
    private int _timeout=5;
    boolean headless =false;
    String headlessBrowser;

    public WebBrowserAgent(){

    }

    /**
     * Constructs a new <tt>WebBrowserAgent</tt> with
     * default configuration file (config.yml) and custom configuration files to
     * featch required parameters
     * @param name a string for naming the creating WebBrowserAgent
     * @param agentParams a map to get the required parameters for creating a WebBrowserAgent
     * @param configurator a configurator instance to provide configuration info for the actions of the WebBrowserAgent
     * @throws Exception
     */

    public WebBrowserAgent(String name, Map<?,?> agentParams, Configurator configurator){
        super(name,agentParams,configurator);
        _logger = FastLogger.getLogger(String.format("%s:WebBrowserAgent",_name));
        _agentParams = agentParams;
        Object headlessObj= _agentParams.get(CONFIG_DESIRED_CAPABILITIES_HEADLESS);
        if(headlessObj !=null){
            headless=headlessObj.toString().equalsIgnoreCase("YES");
        }
        Object headlessBrowserObj = _agentParams.get(CONFIG_HEADLESS_BROSER);
        if (headlessBrowserObj !=null){
            headlessBrowser = headlessBrowserObj.toString();
        }
        Object webRepo =_agentParams.get("webRepo");

        if (webRepo == null){
            repo =(Map<?,?>) configurator.getSettingsMap().get(CONFIG_REPOSITORY_NAME);
        }
        else{
            try{
                repo =(Map<?,?>) readRepo(_agentParams.get("webRepo").toString()).get(CONFIG_REPOSITORY_NAME);
            }catch (Exception e){
                _logger.error(e.getMessage());
            }
        }
    }

    /**
     * Returns a map of all web elements Xpath or CssSelector load from configuration file
     * @return a map
     * <p> Repo information starts with a keyword "Repository" in configuration file
     * <p> A repo contains various pages information
     * <p> A repo contains various web elements, in particular,"trait" is a required keyword and its value can be any Xpath or CssSelector in this page
     * <p> Example:
     * @throws Exception
     */

    public Map<?,?> getRepo(){return repo;}

    /**
     * Sets elements Xpath or CssSelector for actions
     * @param repo a map contains various web elements. Key is a element's name, value is the element's Xpath or CssSelector
     * @throws Exception
     */
    public void setRepo(Map<?,?> repo){this.repo = repo;}

    /**
     *
     * @return current web page
     */
    public String getCurrentPage(){return currentPage;}

    /**
     * Sets a page as current page
     * @param currentPage name of a page
     *<p>Example</>
     * webBrowserAgent,setCurrentPage("LoginPage")
     * @throws Exception
     */
    public void setCurrentPage(String currentPage){this.currentPage = currentPage;}

    protected Map<?,?> readRepo(String repoPath) throws FileNotFoundException, YamlException{
        File dir =new File(repoPath);
        if(dir.isDirectory()){
            File[] files = dir.listFiles();
            for (int i = 0; i <files.length ; i++) {
                if (files[i].isDirectory()){
                    repo = readRepo(files[i].getPath());
                }else if (files[i].isFile()){
                    Map fs =(Map<?,?>) new YamlReader(new FileReader(files[i].getPath())).read();
                    if (repo == null){
                        repo =new HashMap<>();
                        repo.putAll(fs);
                        _logger.info(String.format("Loaded additional repo from: '%s",files[i].getPath()));
                    }
                    else{
                        ((Map) repo.get(CONFIG_REPOSITORY_NAME)).putAll((Map) fs.get(CONFIG_REPOSITORY_NAME));
                        _logger.info(String.format("Loaded addtional repo from: '%s'",files[i].getPath()));
                    }
                }
            }
        }
        else if (dir.isFile()){
            repo = (Map<?,?>) new YamlReader(new FileReader(dir.getPath())).read();
            _logger.info(String.format("Loaded addtional repo from: '%s'",dir.getPath()));
        }
        return repo;
    }

    protected WebDriver getOrCreateWebDriver() throws Exception{
        if(_driver !=null)
            return _driver;
        String driverClassName =_agentParams.get(PARAM_DRIVER_CLASSNAME).toString();
        Object driverRemoteAddressObj = _agentParams.get(PARAM_DRIVER_REMOTEADDRESS);
        Object webDriverPath = _agentParams.get(PARAM_WEB_DRIVER_PATH);
        Object proxyAddress = _agentParams.get(PARAM_PROXY);
        Object arguments = _agentParams.get(PARAM_ARGUMENTS);

        if (headless){
            if (headlessBrowser.equalsIgnoreCase("CHROME")){
                System.setProperty(PROPERTY_CHROME_DRIVER,webDriverPath.toString());
                Class<?> class_=Class.forName(driverClassName);
                Constructor<?> ctor =class_.getConstructor(Capabilities.class);
                ChromeOptions options =new ChromeOptions();
                options.addArguments("--headless");
                options.addArguments("--start-maximized");
                if(arguments !=null && !"".equals(arguments.toString())){
                    options.addArguments(arguments.toString());
                }
                if (_agentParams.get(CONFIG_CHROME_PREFS)!= null){
                    Map<String,Object> chromePrefs=(Map<String,Object>) _agentParams.get(CONFIG_CHROME_PREFS);
                    chromePrefs.putAll(chromePrefs);
                    if(chromePrefs.get(CONFIG_AUTO_DOWNLOAD) !=null
                            && !chromePrefs.get(CONFIG_AUTO_DOWNLOAD).toString().isEmpty()){
                        String path =chromePrefs.get(CONFIG_AUTO_DOWNLOAD).toString();
                        path =new File(path).getAbsolutePath();
                        chromePrefs.put(CONFIG_AUTO_DOWNLOAD,path);
                    }
                    options.setExperimentalOption("prefs",chromePrefs);
                }
                options.setExperimentalOption("useAutomationExtension",false);

                DesiredCapabilities crcapabilities = DesiredCapabilities.chrome();
                crcapabilities.setCapability(ChromeOptions.CAPABILITY,options);
                crcapabilities.setCapability(CapabilityType.ACCEPT_SSL_CERTS,true);
                crcapabilities.setCapability(CapabilityType.ACCEPT_INSECURE_CERTS,true);

                _driver =(WebDriver)ctor.newInstance(crcapabilities);

                return _driver;
            }
            DesiredCapabilities capabilities = DesiredCapabilities.htmlUnit();
            if(proxyAddress != null){
                Proxy proxy =new Proxy();
                proxy.setHttpProxy(proxyAddress.toString());//set your local proxy
                capabilities.setCapability(CapabilityType.PROXY,proxy);
            }
            if(headlessBrowser.equalsIgnoreCase("IE")){
                capabilities.setVersion(BrowserType.IE);
            }
            _driver =new HtmlUnitDriver(capabilities);
            ((HtmlUnitDriver) _driver).setJavascriptEnabled(false);
            _driver.manage().window().maximize();

           return _driver;
        }

        if (driverRemoteAddressObj == null){
            if(driverClassName.equals(CLASS_CHROME_DRIVER)){
                System.setProperty(PROPERTY_CHROME_DRIVER,webDriverPath.toString());
                Class<?> class_=Class.forName(driverClassName);
                Constructor<?> ctor =class_.getConstructor(ChromeOptions.class);
                ChromeOptions options =new ChromeOptions();

                if (_agentParams.containsKey(PARAM_APP_BINARY_PATH)){
                    Object appBinaryPath =_agentParams.get(PARAM_APP_BINARY_PATH);
                    options.setBinary(appBinaryPath.toString());
                }
                options.addArguments("--start-maximized");
                if (arguments !=null && !"".equals(arguments.toString())){
                    for (String aString : arguments.toString().split(";")){
                        options.addArguments(aString);
                    }
                }
                if (_agentParams.get(CONFIG_CHROME_PREFS) !=null && !_agentParams.get(CONFIG_CHROME_PREFS).toString().isEmpty()){
                    Map<String,Object> chromePrefs =(Map<String, Object>) _agentParams.get(CONFIG_CHROME_PREFS);

                    chromePrefs.putAll(chromePrefs);
                    if(chromePrefs.get(CONFIG_AUTO_DOWNLOAD) !=null
                        && ! chromePrefs.get(CONFIG_AUTO_DOWNLOAD).toString().isEmpty()){
                        String path =chromePrefs.get(CONFIG_AUTO_DOWNLOAD).toString();
                        path=new File(path).getAbsolutePath();
                        chromePrefs.put(CONFIG_AUTO_DOWNLOAD,path);
                    }

                    options.setExperimentalOption("prefs",chromePrefs);
                }
                options.setExperimentalOption("useAutomationExtension",false);
                _driver =(WebDriver)ctor.newInstance(options);
            }else if (driverClassName.equals(CLASS_IE_DRIVER)){
                System.setProperty(PROPERTY_IE_DRIVER,webDriverPath.toString());
                DesiredCapabilities dc =DesiredCapabilities.internetExplorer();
                dc.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS,true);
                dc.setCapability("ignoreProtectedModeSettings",true);
                Class<?> class_=Class.forName(driverClassName);
                Constructor<?> ctor =class_.getConstructor(Capabilities.class); //define constructor whose entry parameter's class is Capabilities
                _driver = (WebDriver) ctor.newInstance(dc);
                _driver.manage().window().maximize();
            }
        }
        else{
            String driverRemoteAddress =driverRemoteAddressObj.toString();
            DesiredCapabilities desiredCapabilities=new DesiredCapabilities();
            Map<?,?> desiredCapabilitiesParams =(Map<?,?>)_agentParams.get(CONFIG_DESIRED_CAPABILITIES);
            switch ((desiredCapabilitiesParams.get(CONFIG_DESIRED_CAPABILITIES_PLATFORM)).toString().toLowerCase()){
                case "linux":
                    desiredCapabilities.setPlatform(Platform.LINUX);
                    break;
                case "windows":
                    desiredCapabilities.setPlatform(Platform.WINDOWS);
                    break;
                default:
                    desiredCapabilities.setPlatform(Platform.ANY);
                    break;
            }
            Class<?> class_= Class.forName(driverClassName);
            Constructor<?> ctor =class_.getConstructor(URL.class,Capabilities.class);
            _driver =(WebDriver) ctor.newInstance(new URL(driverRemoteAddress),desiredCapabilities);
            _driver.manage().window().maximize();
        }
        return _driver;
    }

    protected String getRepoElementValue(String name) throws Exception{
        //"name" can be page name ,can be current page's control can be page.control
        int dotpos =name.indexOf('.');
        String pageName;
        String controlName;
        if(dotpos<0){
            pageName =currentPage;
            controlName =name;
        }
        else{
            pageName =name.substring(0,dotpos);
            controlName =name.substring(dotpos+1);
        }
        Map<?,?> pageControlsRepo =(Map<?,?>)repo.get(pageName);
        if(pageControlsRepo == null){
            throw new Exception(String.format("Cannot find control repo for page %s",pageName));
        }
        return pageControlsRepo.get(controlName).toString();
    }

    public CommonStepResult defineLocationVariable(String name,String value) throws Exception{
        String path =getRepoElementValue(name);
        String xpath =path;
        if(path.contains(LOCATION_VARIABLE_NAME))
            xpath =path.replaceAll(LOCATION_VARIABLE_NAME,value);
        CommonStepResult result =new CommonStepResult();
        result.setFieldValue(xpath);
        return result;
    }
    @Override
    public void close() throws Exception {
        if(_driver!=null){
            _driver.quit();
            _driver =null;
        }
    }

    /**
     * Sets the time out duriation for webBrowserAgent
     *
     * @see fast.common.glue.GuiCommonStepDefs#setTimeout(String,int)
     */
    public void setTimeout(int timeout){_timeout= timeout;}

    @Override
    protected void afterException() throws Exception{
        if(headless){ return; }
        Date date= new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        String fileName =dateFormat.format(date) + "png";
        File scrFile =((TakesScreenshot)getOrCreateWebDriver()).getScreenshotAs(OutputType.FILE);
        FileUtils.copyFile(scrFile,new File(".\\ScreenShots\\"+fileName));
    }
    /**
     * Check wheter a string is a Xpath format
     */

    public boolean isXpath(String s){
        String patternStr ="[\\/]+.*((\\'.*[\\/]*.*\\')|(\\\".*[\\/]*.*\\\"))";
        Pattern pattern =Pattern.compile(patternStr);
        Matcher matcher = pattern.matcher(s);
        if(matcher.find()){
            return true;
        }else{
            return false;
        }
    }
    /**
     * Gets currently used web driver
     * @return web driver
     * Required to specify driverClassName and webDriverPath
     * Example in configuratio file
     * driverClassName: 'org.openqa.selenium.ie.InternetExplorerDriver'
     * webDriverPath: drivers/webdriver/IEDriverServer.exe
     * @see #setDriver(WebDriver)
     */
    public WebDriver getDriver() throws Exception{
        return getOrCreateWebDriver();
    }
    /**
     * Sets specified driver
     * @see # getDriver()
     */

    public void setDriver(WebDriver driver){ this._driver=driver;}

    /**
     * Opens the specifiedd web page with given url
     * @param url the address of the web page to open
     * @see fast.common.glue.GuiCommonStepDefs#openUrl(String,String)
     */
    public void openUrl(String url) throws Exception{
        getOrCreateWebDriver().get(url);
    }

    /**
     * Gets url of current page
     * @return a CommonStepResult with url
     * @see fast.common.glue.GuiCommonStepDefs#getCurrentUrl(String)
     */
    public StepResult getCurrentUrl() throws Exception{
        String value = getOrCreateWebDriver().getCurrentUrl();
        CommonStepResult result =new CommonStepResult();
        result.setFieldValue(value);
        return result;
    }

    /**
     * Explicit display wait
     * @param xpath
     * @return
     * @throws Exception
     */
    private WebElement _waitElementVisible(String xpath) throws Exception{
        WebDriverWait wait =new WebDriverWait(getOrCreateWebDriver(),(long)_timeout);
        WebElement el =null;
        if (isXpath(xpath)){
            el = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpath)));
        }else{
            el = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(xpath)));
        }
        return el;
    }
    private WebElement _waitElementClickable(String xpath) throws Exception{
        WebDriverWait wait =new WebDriverWait(getOrCreateWebDriver(),(long)_timeout);
        WebElement el =null;
        if (isXpath(xpath)){
            el = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(xpath)));
        }else{
            el = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(xpath)));
        }
        return el;
    }

    /**
     * Opens the specified web page with the given url
     * @param pageName url of a specified web
     * @see fast.common.glue.GuiCommonStepDefs#onPage(String,String,String)
     */
    public void onPage(String pageName) throws Exception{
        String controlName = pageName + ".trait";
        String xpath =getRepoElementValue(controlName);
        _waitElementVisible(xpath);
        currentPage = pageName;
    }

    /**
     * Checks if  a web element can be found in the current page
     * @param controlName the name of the specified web element
     * @see fast.common.glue.GuiCommonStepDefs#seeControl(String, String) (String,String)
     */
    public void seeControl(String controlName) throws Exception{
        String xpath=getRepoElementValue(controlName);
        _waitElementVisible(xpath);
    }

    /**
     * Left click on the specific web element
     * @param controlName the name of the specified web element
     * @see fast.common.glue.GuiCommonStepDefs#clickControl(String, String)
     */
    public void clickControl(String controlName) throws Exception{
        String xpath=getRepoElementValue(controlName);
        WebElement el =null;
        try{
            el =_waitElementClickable(xpath);
        }catch (Exception e){
            _logger.error("Exception occurs when clickControl. "+ e.getMessage());
            throw e;
        }
        if( el!=null){
            Actions action=new Actions(_driver);
            action.click(el).perform();
        }
    }
    /**
     * Right click on the specific web element
     * @param controlName the name of the specified web element
     * @see fast.common.glue.GuiCommonStepDefs#clickControl(String, String)
     */
    public void rightClickControl(String controlName) throws Exception{
        String xpath=getRepoElementValue(controlName);
        WebElement el =null;
        try{
            el =_waitElementClickable(xpath);
        }catch (Exception e){
            _logger.error("Exception occurs when clickControl. "+ e.getMessage());
            throw e;
        }
        if( el!=null){
            Actions action=new Actions(_driver);
            action.contextClick(el).perform();
        }
    }
    /**
     * Double click on the specific web element
     * @param controlName the name of the specified web element
     * @see fast.common.glue.GuiCommonStepDefs#clickControl(String, String)
     */
    public void doubleClickControl(String controlName) throws Exception{
        String xpath=getRepoElementValue(controlName);
        WebElement el =null;
        try{
            el =_waitElementClickable(xpath);
        }catch (Exception e){
            _logger.error("Exception occurs when clickControl. "+ e.getMessage());
            throw e;
        }
        if( el!=null){
            Actions action=new Actions(_driver);
            action.doubleClick(el).perform();
        }
    }
    /**
     * Types the given text into the specific web element
     * @param text context to type
     * @param controlName the name of the specified web element
     * @see fast.common.glue.GuiCommonStepDefs#typeTextIntoControl(String, String, String)
     */
    public void typeTextIntoControl(String text,String controlName) throws Exception{
        String xpath=getRepoElementValue(controlName);
        _waitElementVisible(xpath).clear();
        _waitElementVisible(xpath).sendKeys(text);
    }



}
