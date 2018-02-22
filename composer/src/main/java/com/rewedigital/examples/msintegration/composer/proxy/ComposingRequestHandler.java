package com.rewedigital.examples.msintegration.composer.proxy;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import com.rewedigital.examples.msintegration.composer.routing.BackendRouting;
import com.rewedigital.examples.msintegration.composer.routing.RouteTypes;
import com.rewedigital.examples.msintegration.composer.session.ResponseWithSession;
import com.rewedigital.examples.msintegration.composer.session.Session;
import com.rewedigital.examples.msintegration.composer.session.SessionLifecycle;
import com.rewedigital.examples.msintegration.composer.session.SessionLifecycleFactory;
import com.spotify.apollo.Request;
import com.spotify.apollo.RequestContext;
import com.spotify.apollo.Response;
import com.spotify.apollo.Status;

import okio.ByteString;

public class ComposingRequestHandler {

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
        final SessionLifecycle sessionLifecycle = sessionLifecycleFactory.build();

        final Request request = context.request();
        final Session session = sessionLifecycle.obtainSession(request);
        
        return routing.matches(request, session).map(
            rm -> rm.routeType(routeTypes)
                .execute(rm, context, session))
            .orElse(defaultResponse(session))
            .thenApply(sessionLifecycle::finalizeSession);
    }

    private static CompletableFuture<ResponseWithSession<ByteString>> defaultResponse(final Session session) {
        final Response<ByteString> response =
            Response.of(Status.INTERNAL_SERVER_ERROR, ByteString.encodeUtf8("Ohh.. noose!"));
        return CompletableFuture
            .completedFuture(new ResponseWithSession<ByteString>(response, session));
    }
}
