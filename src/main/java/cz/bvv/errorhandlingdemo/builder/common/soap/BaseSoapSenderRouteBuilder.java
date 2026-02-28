package cz.bvv.errorhandlingdemo.builder.common.soap;

import cz.bvv.errorhandlingdemo.builder.common.BaseSyncSenderRouteBuilder;
import cz.bvv.errorhandlingdemo.builder.common.routepolicy.FailContractRoutePolicy;
import cz.bvv.errorhandlingdemo.builder.common.routepolicy.RouteConfigurationModule;
import java.util.List;

public abstract class BaseSoapSenderRouteBuilder extends BaseSyncSenderRouteBuilder {
    protected BaseSoapSenderRouteBuilder(List<RouteConfigurationModule> modules,
      FailContractRoutePolicy failureContractRoutePolicy) {
        super(modules, failureContractRoutePolicy);
    }
}
