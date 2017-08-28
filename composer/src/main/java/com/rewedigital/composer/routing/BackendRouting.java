package com.rewedigital.composer.routing;

import java.net.URI;
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
    public Optional<URI> lookup(final Request incommingRequest) {
        try {
            return ruleRouter.match(incommingRequest).map(m -> m.getRule().getTarget());
        } catch (final InvalidUriException e) {
            LOGGER.warn("Could not match the incomming route as the route was not parsable.", e);
            return Optional.empty();
        }
    }

    public enum RoutingType {
        PASS, TEMPLATE
    }

    public static class RouteConfig {
        public RouteConfig(final RoutingType type, final URI backend) {

        }
    }

}
