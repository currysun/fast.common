package fast.common.Reporting;

import java.util.Date;

public class ScenarioRuntimeInfo {

    public static final String DELIMITER="@#@";
    private long scenarioStartRuntime;
    private long scenarioendRuntime;
    private String scenarioId;

    public Date getScenarioStartRuntime() { return new Date(scenarioStartRuntime); }

    public void setScenarioStartRuntime(long scenarioStartRuntime) { this.scenarioStartRuntime = scenarioStartRuntime; }

    public Date getScenarioEndRuntime() { return new Date(scenarioendRuntime); }

    public void setScenarioEndRuntime(long scenarioendRuntime) { this.scenarioendRuntime = scenarioendRuntime; }

    public String getScenarioId() { return scenarioId; }

    public void setScenarioId(String scenarioId) { this.scenarioId = scenarioId; }


}
