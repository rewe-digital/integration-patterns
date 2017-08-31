package com.rewedigital.examples.msintegration.composer.routing;

import com.google.common.collect.ImmutableList;
import com.spotify.apollo.route.Rule;
import com.spotify.apollo.route.RuleRouter;

public class StaticBackendRoutes {

    public static RuleRouter<Match> routes() {
        final Rule<Match> pdp =
            Rule.fromUri("/p/<id>", "GET", Match.of("http://localhost:8080/products/{id}"));
        final Rule<Match> pdpAssets =
            Rule.fromUri("/products/css/<file>", "GET", Match.of("http://localhost:8080/products/css/{file}"));
        final Rule<Match> footerAssets =
            Rule.fromUri("/footer/css/<file>", "GET", Match.of("http://localhost:8081/footer/css/{file}"));
        return RuleRouter.of(ImmutableList.of(pdp, pdpAssets, footerAssets));
    }

    public static class Match {

        private final String backend;

        private Match(final String backend) {
            this.backend = backend;
        }

        public static Match of(final String backend) {
            return new Match(backend);
        }

        public String backend() {
            return backend;
        }

    }

}
