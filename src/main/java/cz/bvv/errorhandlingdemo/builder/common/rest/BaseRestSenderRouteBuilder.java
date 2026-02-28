package cz.bvv.errorhandlingdemo.builder.common.rest;

import cz.bvv.errorhandlingdemo.builder.common.BaseSyncSenderRouteBuilder;
import cz.bvv.errorhandlingdemo.builder.common.routepolicy.FailContractRoutePolicy;
import cz.bvv.errorhandlingdemo.builder.common.routepolicy.RouteConfigurationModule;
import java.util.List;

public abstract class BaseRestSenderRouteBuilder extends BaseSyncSenderRouteBuilder {
    protected BaseRestSenderRouteBuilder(
      List<RouteConfigurationModule> modules,
      FailContractRoutePolicy failureContractRoutePolicy) {
        super(modules, failureContractRoutePolicy);
    }
}
