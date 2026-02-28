package cz.bvv.errorhandlingdemo.builder.sender.demosoap.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class DemoSoapResponse {
    private String status;

    public DemoSoapResponse() {
    }

    public DemoSoapResponse(String status) {
        this.status = status;
    }

}
