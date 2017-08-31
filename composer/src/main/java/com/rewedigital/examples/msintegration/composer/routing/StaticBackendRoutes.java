package com.rewedigital.examples.msintegration.composer.routing;

import com.google.common.collect.ImmutableList;
import com.spotify.apollo.route.Rule;
import com.spotify.apollo.route.RuleRouter;

public class StaticBackendRoutes {

    public static RuleRouter<Match> routes() {
        final Rule<Match> pdp =
            Rule.fromUri("/p/<id>", "GET",
                Match.of("http://localhost:8080/products/{id}", "text/html; charset=UTF-8"));
        final Rule<Match> pdpAssets =
            Rule.fromUri("/products/css/<file>", "GET",
                Match.of("http://localhost:8080/products/css/{file}", "text/css"));
        final Rule<Match> footerAssets =
            Rule.fromUri("/footer/css/<file>", "GET",
                Match.of("http://localhost:8081/footer/css/{file}", "text/css"));
        return RuleRouter.of(ImmutableList.of(pdp, pdpAssets, footerAssets));
    }

    public static class Match {

        private final String backend;
        private final String contentType;

        private Match(final String backend, final String contentType) {
            this.backend = backend;
            this.contentType = contentType;
        }

        public static Match of(final String backend, final String contentType) {
            return new Match(backend, contentType);
        }

        public String backend() {
            return backend;
        }

        public String contentType() {
            return contentType;
        }

    }

}
