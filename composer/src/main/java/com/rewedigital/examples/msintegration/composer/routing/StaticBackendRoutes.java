package com.rewedigital.examples.msintegration.composer.routing;

import static com.rewedigital.examples.msintegration.composer.routing.StaticBackendRoutes.RouteType.PROXY;
import static com.rewedigital.examples.msintegration.composer.routing.StaticBackendRoutes.RouteType.TEMPLATE;

import com.google.common.collect.ImmutableList;
import com.spotify.apollo.route.Rule;
import com.spotify.apollo.route.RuleRouter;

public class StaticBackendRoutes {

    public static RuleRouter<Match> routes() {
        final Rule<Match> home =
            Rule.fromUri("/", "GET", Match.of("https://www.rewe-digital.com/", PROXY));
        final Rule<Match> homeAssets =
            Rule.fromUri("/assets/<path:path>", "GET", Match.of("https://www.rewe-digital.com/assets/{path}", PROXY));


        final Rule<Match> pdp =
            Rule.fromUri("/p/<id>", "GET", Match.of("http://localhost:8080/products/{id}", TEMPLATE));
        final Rule<Match> pdpAssets =
            Rule.fromUri("/products/css/<file>", "GET", Match.of("http://localhost:8080/products/css/{file}", PROXY));
        final Rule<Match> footerAssets =
            Rule.fromUri("/footer/css/<file>", "GET", Match.of("http://localhost:8081/footer/css/{file}", PROXY));

        return RuleRouter.of(ImmutableList.of(home, homeAssets, pdp, pdpAssets, footerAssets));
    }

    public enum RouteType {
        PROXY, TEMPLATE;
    }

    public static class Match {

        private final String backend;
        private final RouteType routeType;

        private Match(final String backend, final RouteType routeType) {
            this.backend = backend;
            this.routeType = routeType;
        }

        public static Match of(final String backend, final RouteType routeType) {
            return new Match(backend, routeType);
        }

        public String backend() {
            return backend;
        }

        public boolean shouldProxy() {
            return RouteType.PROXY == routeType;
        }

    }

}
