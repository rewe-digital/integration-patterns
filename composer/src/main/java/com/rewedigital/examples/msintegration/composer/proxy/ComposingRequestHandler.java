package com.rewedigital.examples.msintegration.composer.proxy;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rewedigital.examples.msintegration.composer.routing.BackendRouting;
import com.rewedigital.examples.msintegration.composer.routing.BackendRouting.RouteMatch;
import com.rewedigital.examples.msintegration.composer.routing.RouteTypes;
import com.rewedigital.examples.msintegration.composer.session.Session;
import com.rewedigital.examples.msintegration.composer.session.SessionLifecycleFactory;
import com.rewedigital.examples.msintegration.composer.session.SessionLifecycle;
import com.spotify.apollo.Request;
import com.spotify.apollo.RequestContext;
import com.spotify.apollo.Response;
import com.spotify.apollo.Status;

import okio.ByteString;

public class ComposingRequestHandler {

    private static final CompletableFuture<Response<ByteString>> ERROR_PAGE = CompletableFuture
        .completedFuture(Response.of(Status.INTERNAL_SERVER_ERROR, ByteString.encodeUtf8("Ohh.. noose!")));

    private static final Logger LOGGER = LoggerFactory.getLogger(ComposingRequestHandler.class);

    private final BackendRouting routing;
    private final RouteTypes routeTypes;
    private final SessionLifecycleFactory sessionLifecycleFactory;

    public ComposingRequestHandler(final BackendRouting routing, final RouteTypes routeTypes,
        final SessionLifecycleFactory sessionLifecycleFactory) {
        this.routing = Objects.requireNonNull(routing);
        this.routeTypes = Objects.requireNonNull(routeTypes);
        this.sessionLifecycleFactory = Objects.requireNonNull(sessionLifecycleFactory);
    }

    public CompletionStage<Response<ByteString>> execute(final RequestContext context) {
        final Request request = context.request();
        final SessionLifecycle sessionLifecylce = sessionLifecycleFactory.build();
        
        final Session session = sessionLifecylce.obtainSession(request);
        final Optional<RouteMatch> match = routing.matches(request, session);
        return match.map(rm -> {
            LOGGER.debug("The request {} matched the backend route {}.", request, match);
            return rm.routeType(routeTypes).execute(rm, context, session)
                .thenApply(r -> r.writeSessionToResponse(sessionLifecylce));
        }).orElse(defaultResponse());
    }

    private static CompletableFuture<Response<ByteString>> defaultResponse() {
        return ERROR_PAGE;
    }
}
