package com.rewedigital.examples.msintegration.composer.routing;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rewedigital.examples.msintegration.composer.routing.StaticBackendRoutes.Match;
import com.spotify.apollo.Request;
import com.spotify.apollo.route.InvalidUriException;
import com.spotify.apollo.route.RuleRouter;

public class BackendRouting {

    private static final Logger LOGGER = LoggerFactory.getLogger(BackendRouting.class);
    private final RuleRouter<Match> ruleRouter;

    public BackendRouting(final RuleRouter<Match> ruleRouter) {
        this.ruleRouter = Objects.requireNonNull(ruleRouter);
    }

    public Optional<RouteMatch> matches(final Request incommingRequest) {
        try {
            return ruleRouter.match(incommingRequest)
                .map(m -> new RouteMatch(m.getRule().getTarget(), m.parsedPathArguments()));
        } catch (final InvalidUriException e) {
            LOGGER.warn("Could not match the incomming route as the route was not parsable.", e);
            return Optional.empty();
        }
    }

    public static class RouteMatch {
        private final Match backend;
        private final Map<String, String> parsedPathArguments;

        public RouteMatch(final Match backend, final Map<String, String> parsedPathArguments) {
            this.backend = backend;
            this.parsedPathArguments = parsedPathArguments;
        }

        public String backend() {
            return backend.backend();
        }

        public String contentType() {
            return backend.contentType();
        }

        public Map<String, String> parsedPathArguments() {
            return parsedPathArguments;
        }

        @Override
        public String toString() {
            return "RouteMatch [backend=" + backend + ", parsedPathArguments=" + parsedPathArguments + "]";
        }
    }

}
