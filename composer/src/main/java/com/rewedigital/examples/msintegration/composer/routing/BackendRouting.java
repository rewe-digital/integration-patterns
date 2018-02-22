package com.rewedigital.examples.msintegration.composer.routing;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.damnhandy.uri.template.UriTemplate;
import com.rewedigital.examples.msintegration.composer.routing.StaticBackendRoutes.Match;
import com.rewedigital.examples.msintegration.composer.session.Session;
import com.spotify.apollo.Request;
import com.spotify.apollo.route.InvalidUriException;
import com.spotify.apollo.route.RuleMatch;
import com.spotify.apollo.route.RuleRouter;

public class BackendRouting {

    private static final Logger LOGGER = LoggerFactory.getLogger(BackendRouting.class);
    private final RuleRouter<Match> ruleRouter;

    public BackendRouting(final RuleRouter<Match> ruleRouter) {
        this.ruleRouter = Objects.requireNonNull(ruleRouter);
    }

    public Optional<RouteMatch> matches(final Request incommingRequest, final Session session) {
        try {
            return findMatch(incommingRequest)
                .map(m -> new RouteMatch(m.getRule().getTarget(), m.parsedPathArguments()));
        } catch (final InvalidUriException e) {
            LOGGER.warn("Could not match the incomming route as the route was not parsable.", e);
            return Optional.empty();
        }
    }

    private Optional<RuleMatch<Match>> findMatch(final Request incommingRequest) throws InvalidUriException {
        final Optional<RuleMatch<Match>> match = ruleRouter.match(incommingRequest);
        match.ifPresent(m -> LOGGER.debug("The request {} matched the backend route {}.", incommingRequest, m));
        return match;
    }

    public static class RouteMatch {

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

}
