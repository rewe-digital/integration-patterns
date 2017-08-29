package com.rewedigital.composer.routing;

import java.net.URI;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.spotify.apollo.Request;
import com.spotify.apollo.route.InvalidUriException;
import com.spotify.apollo.route.RuleRouter;

public class BackendRouting {

    private static final Logger LOGGER = LoggerFactory.getLogger(BackendRouting.class);
    private final RuleRouter<URI> ruleRouter;

    public BackendRouting(final RuleRouter<URI> ruleRouter) {
        this.ruleRouter = Objects.requireNonNull(ruleRouter);
    }

    /**
     * The location of a back-end service that provides the content for a matched route.
     *
     * @param incommingRequest
     * @return the location of the back-end service or {@link Optional#empty()} when the request could not be parsed.
     */
    public Optional<RouteMatch> lookup(final Request incommingRequest) {
        try {
            return ruleRouter.match(incommingRequest)
                .map(m -> new RouteMatch(m.getRule().getTarget(), m.parsedPathArguments()));
        } catch (final InvalidUriException e) {
            LOGGER.warn("Could not match the incomming route as the route was not parsable.", e);
            return Optional.empty();
        }
    }

    public static class RouteMatch {
        private final URI backend;
        private final Map<String, String> parsedPathArguments;

        public RouteMatch(final URI backend, final Map<String, String> parsedPathArguments) {
            this.backend = backend;
            this.parsedPathArguments = parsedPathArguments;
        }

        public URI backend() {
            return backend;
        }

        public Map<String, String> parsedPathArguments() {
            return parsedPathArguments;
        }
    }

}
