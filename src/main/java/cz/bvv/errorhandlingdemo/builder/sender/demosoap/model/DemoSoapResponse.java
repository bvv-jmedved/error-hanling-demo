package cz.bvv.errorhandlingdemo.builder.sender.demosoap.model;

public class DemoSoapResponse {
    private String status;

    public DemoSoapResponse() {
    }

    public DemoSoapResponse(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
