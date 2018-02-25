package com.rewedigital.examples.msintegration.composer.routing;

public enum RouteTypeName {
    PROXY {
        @Override
        public RouteType from(final RouteTypes routeTypes) {
            return routeTypes.proxy();
        }
    },
    TEMPLATE {
        @Override
        public RouteType from(final RouteTypes routeTypes) {
            return routeTypes.template();
        }
    };

    public abstract RouteType from(RouteTypes routeTypes);
}