package fast.common.context;

import java.util.ArrayList;

public abstract class StepResult {

    public static final String DEFAULT_FIELD_VALUE="Value";

    public enum Status{
        Passed,
        Failed,
        Skipped
    }

    public static final String STEP_RESULT_PASS ="PASSED";
    public static final String STEP_RESULT_FAIL ="FAILED";
    public static final String STEP_RESULT_SKIP ="SKIPPED";

    private Status status;
    private Status log;
    private Status failedMessage;

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Status getLog() {
        return log;
    }

    public void setLog(Status log) {
        this.log = log;
    }

    public Status getFailedMessage() {
        return failedMessage;
    }

    public void setFailedMessage(Status failedMessage) {
        this.failedMessage = failedMessage;
    }

    public abstract String toString();
    public abstract String getFieldValue(String field) throws Throwable;
    public abstract ArrayList<String> getFieldValues(String field);
}
