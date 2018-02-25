package com.rewedigital.examples.msintegration.composer.routing;

public class Match {

    private final String backend;
    private final RouteTypeName routeType;

    private Match(final String backend, final RouteTypeName routeType) {
        this.backend = backend;
        this.routeType = routeType;
    }

    public static Match of(final String backend, final RouteTypeName routeType) {
        return new Match(backend, routeType);
    }

    public String backend() {
        return backend;
    }

    public RouteType routeType(final RouteTypes routeTypes) {
        return routeType.from(routeTypes);
    }
}