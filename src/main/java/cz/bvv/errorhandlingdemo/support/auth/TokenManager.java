package cz.bvv.errorhandlingdemo.support.auth;

import org.apache.camel.Exchange;

public interface TokenManager {
    void refreshToken(Exchange exchange);
}
