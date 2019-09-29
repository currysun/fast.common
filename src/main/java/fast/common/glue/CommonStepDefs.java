package fast.common.glue;

import cucumber.api.Scenario;
import cucumber.api.java.en.When;
import fast.common.agents.AgentsManager;
import fast.common.agents.WebApiAgent;
import fast.common.context.HelperMethods;
import fast.common.context.StepResult;
import fast.common.context.WebApiResult;
import fast.common.logging.FastLogger;
import org.junit.After;
import org.junit.Before;

import java.util.Map;
import java.util.Set;

public class CommonStepDefs extends BaseCommonStepDefs{
    public static final FastLogger logger =FastLogger.getLogger("CommonStepDefs");

    /**
     * Use hook @before to setup rumtime environment before each scenario
     */

    @Before
    @Override
    public void beforeScenario(Scenario scenario) throws Exception{
        super.beforeScenario(scenario);
    }

    @After
    @Override
    public void afterScenario(Scenario scenario) throws Exception {
        Set<String> tag37set = getScenarioContext().getTag37set();
        String tag37list = null;
        if (tag37set != null) {
            tag37list = String.join(", ", getScenarioContext().getTag37set());
        }

        runtimeInfoManager.notifyScenarioEnd(scenario, this);
        if (runtimeInfoManager.isScenarioEnded(scenario)) {
            runtimeInfoManager.saveReportToFile(scenario);
            AgentsManager.getInstance().flushBuffersToLog();
            getScenarioContext().close();
            ;

            if (tag37list != null && !tag37list.isEmpty()) {
                scenarioAndLogWrite(String.format("Scenario ID: '%s' FINISHED with status: '%s',TAGS 37: [%s]",
                        scenario.getId(), scenario.getStatus(), tag37list));
            } else {
                scenarioAndLogWrite(String.format("Scenario ID: '%s' FINISHEDwith status: '%s'", scenario.getId(),
                        scenario.getStatus()));
            }
            scenarioAndLogWrite("-----------------------------------------");
        }
    }
        @When("^Wait (\\d+) seconds$")
        public void waitSeconds(int sec) throws Throwable{
            Thread.sleep(sec* 1_000L);
        }

    /**
     * Closes the corresponding agent
     */
    @When("^(\\w+) exit$")
    public void exit(String agentName) throws Exception{
        AgentsManager.getInstance().getOrCreateAgent(agentName).close();
    }

    /**
     * Execute send request and saves results into a variable.
     * @param agentName
     * @param type
     * @param uri
     * @param varName a variable to save resutls
     * @throws Throwable
     */
    @When("^(\\w+) send request type \"([^\"]*)\" uri \"([^\"]*)\" into (@\\w+)$")
    public void sendRequest(String agentName,String type,String uri,String varName) throws Throwable{
        WebApiAgent.RequestType processedType = WebApiAgent.RequestType.valueOf(getScenarioContext().processString(type));
        String processedUri =getScenarioContext().processString(uri);
        WebApiAgent webApiAgent= AgentsManager.getInstance().getOrCreateAgent(agentName);
        StepResult result= webApiAgent.sendRequest(processedType,processedUri);
        getScenarioContext().saveLastStepResult(result,varName);
    }

    /**
     * Execute send rest request and saves results into a variable.
     * @param agentName the name of agent on which to run the step
     * @param type then send request type
     * @param uri the send request uri address
     * @param pathParams the uri address path params
     * @param params the send request params
     * @param body the send request body
     * @param varName
     * @throws Throwable
     */
    @When("^(\\w+) send request type \"([^\"]*)\" uri \"([^\"]*)\" pathParams \"([^\"]*)\" params \"([^\"]*)\" body \"([^\"]*)\" into (@\\w+)$")
    public void sendRestRequest(String agentName,String type,String uri,String pathParams,String params,String body,String varName) throws Throwable{
        WebApiAgent.RequestType processedType = WebApiAgent.RequestType.valueOf(getScenarioContext().processString(type));
        String processedUri =getScenarioContext().processString(uri);
        Map<String,Object> processedPathParams = HelperMethods.processStringToMap(pathParams);
        Map<String,Object> processedParams = HelperMethods.processStringToMap(params);
        Map<String,Object> processedBody = HelperMethods.processStringToMap(body);
        WebApiAgent webApiAgent = AgentsManager.getInstance().getOrCreateAgent(agentName);
        StepResult result =webApiAgent.sendRestRequest(processedType,processedUri,processedPathParams,processedParams,processedBody);
        getScenarioContext().saveLastStepResult(result,varName);
    }


}
