package com.rewedigital.examples.msintegration.composer.routing;

import com.google.common.collect.ImmutableList;
import com.spotify.apollo.route.Rule;
import com.spotify.apollo.route.RuleRouter;

public class StaticBackendRoutes {

    public static RuleRouter<String> routes() {
        final Rule<String> pdp = Rule.fromUri("/p/<id>", "GET", "http://localhost:8080/products/{id}");
        return RuleRouter.of(ImmutableList.of(pdp));
    }

}