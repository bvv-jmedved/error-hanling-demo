package cz.bvv.errorhandlingdemo.poc;

public enum FailureStep {
    SENDER_VALIDATE("sender-validate", "Sender validation"),
    SENDER_AUTH("sender-auth", "Sender authorization"),
    PROCESS_TRANSFORM_REQUEST("process-transform-request", "Process transform request"),
    PROCESS_TRANSFORM_RESPONSE("process-transform-response", "Process transform response"),
    TECHNICAL_CALL("technical-call", "Technical call");

    private final String headerValue;
    private final String description;

    FailureStep(String headerValue, String description) {
        this.headerValue = headerValue;
        this.description = description;
    }

    public String headerValue() {
        return headerValue;
    }

    public String description() {
        return description;
    }
}
