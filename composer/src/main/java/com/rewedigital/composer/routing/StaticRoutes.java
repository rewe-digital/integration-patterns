package com.rewedigital.composer.routing;

import java.net.URI;

import com.google.common.collect.ImmutableList;
import com.spotify.apollo.route.Rule;
import com.spotify.apollo.route.RuleRouter;

public class StaticRoutes {

    public static RuleRouter<URI> routes() {
        final Rule<URI> pdp = Rule.fromUri("/p/<id>", "GET", URI.create("http://localhost:8080/products/"));
        return RuleRouter.of(ImmutableList.of(pdp));
    }

}
