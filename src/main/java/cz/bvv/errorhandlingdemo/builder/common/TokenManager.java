package cz.bvv.errorhandlingdemo.builder.common;

import org.apache.camel.Exchange;

public interface TokenManager {
    void refreshToken(Exchange exchange);
}
