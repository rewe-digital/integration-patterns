package com.rewedigital.examples.msintegration.composer.routing;

import java.util.Objects;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rewedigital.examples.msintegration.composer.session.SessionRoot;
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

    public Optional<RouteMatch> matches(final Request incommingRequest, final SessionRoot session) {
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

}
