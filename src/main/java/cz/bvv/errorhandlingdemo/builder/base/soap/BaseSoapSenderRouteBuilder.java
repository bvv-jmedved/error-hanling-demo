package cz.bvv.errorhandlingdemo.builder.base.soap;

import cz.bvv.errorhandlingdemo.builder.base.BaseSyncSenderRouteBuilder;
import cz.bvv.errorhandlingdemo.policy.route.FailContractRoutePolicy;
import cz.bvv.errorhandlingdemo.policy.route.RouteConfigurationModule;
import java.util.List;

public abstract class BaseSoapSenderRouteBuilder extends BaseSyncSenderRouteBuilder {
    protected BaseSoapSenderRouteBuilder(List<RouteConfigurationModule> modules,
      FailContractRoutePolicy failureContractRoutePolicy) {
        super(modules, failureContractRoutePolicy);
    }
}
