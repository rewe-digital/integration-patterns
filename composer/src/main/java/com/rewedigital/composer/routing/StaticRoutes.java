package com.rewedigital.composer.routing;

import java.net.URI;

import com.google.common.collect.ImmutableList;
import com.spotify.apollo.route.Rule;
import com.spotify.apollo.route.RuleRouter;

public class StaticRoutes {

    public static RuleRouter<URI> routes() {
        final Rule<URI> heise = Rule.fromUri("/heise/<id>", "GET", URI.create("https://www.heise.de"));
        final Rule<URI> golem = Rule.fromUri("/golem/<id>", "GET", URI.create("https://www.golem.de"));
        return RuleRouter.of(ImmutableList.of(heise, golem));
    }

}
