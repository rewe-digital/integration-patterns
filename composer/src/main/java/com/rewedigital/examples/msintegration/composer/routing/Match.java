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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((backend == null) ? 0 : backend.hashCode());
        result = prime * result + ((routeType == null) ? 0 : routeType.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final Match other = (Match) obj;
        if (backend == null) {
            if (other.backend != null)
                return false;
        } else if (!backend.equals(other.backend))
            return false;
        if (routeType != other.routeType)
            return false;
        return true;
    }


}
