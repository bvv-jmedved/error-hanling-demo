package cz.bvv.errorhandlingdemo.poc;

/**
 * PoC-only component used to simulate failures and retry scenarios for the architecture demo.
 *
 * <p>Types in this package are intentionally non-architectural and exist only
 * to make behavior observable in tests and local runs.
 */
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
