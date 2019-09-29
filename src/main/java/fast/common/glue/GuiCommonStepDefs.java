package fast.common.glue;

import cucumber.api.Scenario;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import fast.common.agents.AgentsManager;
import fast.common.context.StepResult;
import fast.common.logging.FastLogger;
import gherkin.formatter.model.Step;

import java.util.List;


public class GuiCommonStepDefs extends BaseCommonStepDefs{
    private static FastLogger logger= FastLogger.getLogger("GUICommonStepDefs");

    @Before
    public void beforeScenario(Scenario scenario) throws Exception{
        super.beforeScenario(scenario);
    }

    @After
    public void afterScenario(Scenario scenario) throws Exception{
        super.afterScenario(scenario);
    }

    /**
     * Sets the time out duration for the corresponding agent
     * @param agentName name of agent on which to run the step
     * @param timeout time duration in seconds
     *
     * @see fast.common.agents.WebBrowserAgent#setTimeout(int)
     * @throws Throwable
     */
    @When("^(\\w+) set timeout (\\d+) seconds$")
    public void setTimeout(String agentName, int timeout) throws Throwable{
        AgentsManager.getInstance().getOrCreateAgent(agentName).run("setTimeout",timeout);
    }

    /**
     * Open the specified web pagewith the given url, specially for the web agent.
     *
     * @see fast.common.agents.WebBrowserAgent.openUrl(String)
     */
    @When("^(\\w+) open \"([^\"]*)\" url$")
    public void openUrl(String agentName,String url) throws Throwable{
        String processedUrl =getScenarioContext().processString(url);
        AgentsManager.getInstance().getOrCreateAgent(agentName).run("openUrl",processedUrl);
    }

    /**
     * Gets url of the curent web page, specially for the web agent.
     *
     * @see fast.common.agents.WebBrowserAgent#getCurrentUrl()
     */
    @Then("^(\\w+) get current url$")
    public String getCurrentUrl(String agentName) throws Throwable{
        StepResult result = AgentsManager.getInstance().getOrCreateAgent(agentName).runWithResult("getCurrentUrl");
        String url =result.getFieldValue(StepResult.DEFAULT_FIELD_VALUE);
        logger.info("Current Url: ["+url +"]");
        return url;
    }

    /**
     * Sets the current web page
     * @param agentName the name of agent on which to run the step
     * @param pageName the name of the page to set as current page,referenced in web repository
     * <p>
     * <blockquote><pre>@Then("^(\\w+)(am|is) on ([\\w\\.]+)")</pre></blockquote>
     * <blockquote><pre>The WebAgent is on SearchPage</pre></blockquote>
     *
     * @see fast.common.agents.WebBrowserAgent#onPage(String)
     */
    @Then("^(\\w+) (am|is) on ([\\w\\.]+)")
    public void onPage(String agentName,String verbNotUsed,String pageName) throws Throwable{
        String processedPageName =getScenarioContext().processString(pageName);
        AgentsManager.getInstance().getOrCreateAgent(agentName).run("onPage",processedPageName);
    }

    /**
     * Checks whether the specified control can be found in the current runtime
     * @param agentName the name of agent on which to run the step
     * @param controlName the name of the page to set as current page,referenced in web repository
     * <p>
     * <blockquote><pre>@Then("^(\\w+) see ([\\w\\.]+)")</pre></blockquote>
     * <blockquote><pre>The WebAgent is on SearchPage</pre></blockquote>
     *
     * @see fast.common.agents.WebBrowserAgent#seeControl(String)
     */
    @Then("^(\\w+) see ([\\w\\.]+)")
    public void seeControl(String agentName,String controlName) throws Throwable{
        String processedControlName =getScenarioContext().processString(controlName);
        AgentsManager.getInstance().getOrCreateAgent(agentName).run("seeControl",processedControlName);
    }
    /**
     * Left Clock on the specified control
     * @param agentName the name of agent on which to run the step
     * @param controlName the name of the page to set as current page,referenced in web repository
     * <p>Pattern:
     * <blockquote><pre>@When("^(\\w+) click on ([\\w\\.]+)")</pre></blockquote>
     * <blockquote><pre>The WebAgent click on LogonButton</pre></blockquote>
     *
     * @see fast.common.agents.WebBrowserAgent#clickControl(String)
     */
    @When("^(\\w+) click on ([\\w\\.]+)")
    public void clickControl(String agentName,String controlName) throws Throwable{
        String processedControlName =getScenarioContext().processString(controlName);
        AgentsManager.getInstance().getOrCreateAgent(agentName).run("clickControl",processedControlName);
    }

    /**
     * Types the given text into the specified control
     * @param agentName the name of agent on which to run the step
     * @param text the ext to type
     * @param controlName the name of the page to set as current page,referenced in web repository
     * <p>Pattern:
     * <blockquote><pre>@When("^(\\w+) type \"([^\"]*)\" ([\\w\\.]+)")</pre></blockquote>
     * <blockquote><pre>The WebAgent type "cucumber" into SearchTextbox</pre></blockquote>
     *
     * @see fast.common.agents.WebBrowserAgent#typeTextIntoControl(String,String)
     */
    @When("^(\\w+) type \"([^\"]*)\" into ([\\w\\.]+)")
    public void typeTextIntoControl(String agentName,String text,String controlName) throws Throwable{
        String processedText =getScenarioContext().processString(text);
        String processedControlName =getScenarioContext().processString(controlName);
        AgentsManager.getInstance().getOrCreateAgent(agentName).run("typeTextIntoControl",processedText,processedControlName);
    }

    @Then("^(\\w+) read all text from (@?\\w+) into List")
    public List<String> readAllTextOnControl(String agentName, String controlName) throws Throwable{
        String processControlName =(controlName.startsWith("@"))? getScenarioContext()
                .processString(controlName+".Value"):controlName;
        StepResult result =AgentsManager.getInstance().getOrCreateAgent(agentName).runWithResult("readAllTextOnControl",
                processControlName);
        return  result.getFieldValues(StepResult.DEFAULT_FIELD_VALUE);
    }
    @When("^(\\w+) scroll current page to width:([-\\+]?[\\d]+) height:([-\\+])?[\\d]+]$")
    public void scrollPage(String agentName,int widthOffset, int heightOffset) throws Exception{
        AgentsManager.getInstance().getOrCreateAgent(agentName).run("scrollPage",widthOffset,heightOffset);
    }

    @When("^(\\w+) scroll on ([\\w\\.+] by offset ([-\\+]?[\\d]+))$")
    public void scrollPage(String agentName,String controlName, String offset) throws Exception{
        String processedControlName =getScenarioContext().processString(controlName);
        String processOffset =getScenarioContext().processString(offset);
        AgentsManager.getInstance().getOrCreateAgent(agentName).run("scrollTo",processedControlName,processOffset);
    }

    /**
     *
     * @param agentName
     * @param controlName1
     * @param controlName2
     * @param x
     * @param y
     * <pre>When WebAgent drag from Change and drop to ReportArea by offset 300,300<pre/>
     * @throws Exception
     */
    @When("^(\\w+) drag from ([\\w\\.]+) and drop to ([\\w\\.]+) by offset ([-\\+]?[\\d]+),([-\\+]?[\\d]+)$")
    public void dragAndDrop(String agentName,String controlName1,String controlName2,String x,String y) throws Exception{
        String processedControlName1 =getScenarioContext().processString(controlName1);
        String processedControlName2 =getScenarioContext().processString(controlName2);
        AgentsManager.getInstance().getOrCreateAgent(agentName).run("dragAndDrop",processedControlName1,processedControlName2);
    }

    @When("^(\\w+) mouse over to control (@?\\w+)$")
    public void moveTo(String agentName,String controlName) throws Throwable{
        String processControlName = (controlName.contains("@")) ? getScenarioContext()
                .processString(controlName+".Value"):controlName;
        AgentsManager.getInstance().getOrCreateAgent(agentName).run("moveTo",processControlName);
    }



}
