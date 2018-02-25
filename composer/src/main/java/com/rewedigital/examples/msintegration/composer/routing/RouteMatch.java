package com.rewedigital.examples.msintegration.composer.routing;

import java.util.Collections;
import java.util.Map;

import com.damnhandy.uri.template.UriTemplate;

public class RouteMatch {

    private final Match backend;
    private final Map<String, Object> parsedPathArguments;

    public RouteMatch(final Match backend, final Map<String, String> parsedPathArguments) {
        this.backend = backend;
        this.parsedPathArguments = Collections.<String, Object>unmodifiableMap(parsedPathArguments);
    }

    public String backend() {
        return backend.backend();
    }

    public RouteType routeType(final RouteTypes routeTypes) {
        return backend.routeType(routeTypes);
    }

    public Map<String, Object> parsedPathArguments() {
        return parsedPathArguments;
    }

    @Override
    public String toString() {
        return "RouteMatch [backend=" + backend + ", parsedPathArguments=" + parsedPathArguments + "]";
    }

    public String expandedPath() {
        return UriTemplate.fromTemplate(backend.backend()).expand(parsedPathArguments);
    }
}