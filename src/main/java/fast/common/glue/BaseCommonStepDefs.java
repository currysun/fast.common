package fast.common.glue;

import cucumber.api.Scenario;
import fast.common.Reporting.RuntimeInfoManager;
import fast.common.agents.AgentsManager;
import fast.common.context.ScenarioContext;
import fast.common.context.ScenarioContextManager;
import fast.common.logging.FastLogger;
import org.codehaus.plexus.util.StringUtils;

public class BaseCommonStepDefs {
    private static FastLogger logger= FastLogger.getLogger("BaseCommonStepDefs");
    private static ThreadLocal<ScenarioContext> _scenarioContext= new ThreadLocal<>();
    static RuntimeInfoManager runtimeInfoManager=RuntimeInfoManager.INSTANCE;

    public void scenarioAndLogWrite(String str){
        logger.info(str);
        if (getScenarioContext().getScenario()!=null){
            Scenario scenario =getScenarioContext().getScenario();
            scenario.write(str);
        }
    }

    public static ScenarioContext getScenarioContext(){
        return _scenarioContext.get();
    }

    public void beforeScenario(Scenario scenario) throws Exception {
        if(!runtimeInfoManager.isScenarioStarted(scenario)){
            //Setup logger thread context
            setupContextForLogger(scenario);
            ScenarioContext scenarioContext= ScenarioContextManager.getInstance().getOrCreateScenarioContext(scenario);
            _scenarioContext.set(scenarioContext);

            // Cucumber Gherkin issue - Write to cucumber scenario object raises exception in Before step so will in logger.
            logger.info("----------------------------------------------");
            logger.info(String.format("Scenario ID: '%s' STARTED",scenario.getId()));
          }
          runtimeInfoManager.notifyScenarioStart(scenario,this);
    }

    //This function sets up thread context keys that can be used by log4j2
    //to get feature name, scenario name and unique Id generated.
    private void setupContextForLogger(Scenario scenario) {
        String scenarioId= scenario.getId();
        String scenarioName= scenario.getName();
        String featureName= "";
        String counter= "";
        //get Feature name
        int indexEnd = scenarioId.indexOf(";");
        if (indexEnd !=-1){
            featureName = scenarioId.substring(0,indexEnd).replace("-"," ");
        }
        //Get data set counter in case scenario outline
        indexEnd =scenarioId.lastIndexOf(";");
        if (indexEnd!=-1){
            counter = scenarioId.substring(indexEnd + 1,scenarioId.length());
            if (!StringUtils.isNumeric(counter)){
                counter ="";
            }
        }
        logger.setThreadContextValue("featureName",featureName);
        logger.setThreadContextValue("scenarioName",scenarioName);
        logger.setThreadContextValue("testName",generateTestName(featureName,scenarioName,counter));
        logger.setThreadContextValue("shortTestName",generateShortTestName(featureName,scenarioName,counter));


    }

    private String generateTestName(String featureName, String scenarioName, String counter) {
        String uniqueName =featureName +"_"+ scenarioName;
        if (counter !=null && !counter.isEmpty())
            uniqueName = uniqueName + "_"+ counter;
        return removeInvalidChars(uniqueName);
    }

    private String removeInvalidChars(String name) {
        return name.replaceAll("[^a-zA-Z0-9\\.\\-]","_");
    }

    private String generateShortTestName(String featureName, String scenarioName, String counter) {
        String shortFeatureName = featureName.length()>10 ?featureName.substring(0,10): featureName;
        String shortScenarioName = scenarioName.length()>10 ?scenarioName.substring(0,10): featureName;

        String shortName = shortFeatureName+ "_"+ shortScenarioName;

        if (counter !=null && !counter.isEmpty())
            shortName = shortName +"_"+counter;
        return removeInvalidChars(shortName);
    }

    public void afterScenario(Scenario scenario) throws Exception{
        runtimeInfoManager.notifyScenarioEnd(scenario,this);
        if(runtimeInfoManager.isScenarioEnded(scenario)){
            runtimeInfoManager.saveReportToFile(scenario);
            //Done : if total count of threads ==1 then clear all buffers
            //and even proably kill agents

            AgentsManager.getInstance().flushBuffersToLog();
            if(getScenarioContext() !=null)
                getScenarioContext().close();
        }
    }
}
