package com.rewedigital.examples.msintegration.composer.routing;

import com.google.common.collect.ImmutableList;
import com.spotify.apollo.route.Rule;
import com.spotify.apollo.route.RuleRouter;

public class StaticBackendRoutes {

    public static RuleRouter<String> routes() {
        final Rule<String> pdp = Rule.fromUri("/p/<id>", "GET", "http://localhost:8080/products/{id}");
        final Rule<String> pdpAssets = Rule.fromUri("/products/css/<file>", "GET",
                "http://localhost:8080/products/css/{file}");
        final Rule<String> footerAssets = Rule.fromUri("/footer/css/<file>", "GET",
                "http://localhost:8081/footer/css/{file}");
        return RuleRouter.of(ImmutableList.of(pdp, pdpAssets, footerAssets));
    }

}