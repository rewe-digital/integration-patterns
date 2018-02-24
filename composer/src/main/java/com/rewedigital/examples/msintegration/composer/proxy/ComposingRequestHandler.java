package com.rewedigital.examples.msintegration.composer.proxy;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import com.rewedigital.examples.msintegration.composer.routing.BackendRouting;
import com.rewedigital.examples.msintegration.composer.routing.RouteTypes;
import com.rewedigital.examples.msintegration.composer.session.ResponseWithSession;
import com.rewedigital.examples.msintegration.composer.session.SessionHandler;
import com.rewedigital.examples.msintegration.composer.session.SessionHandlerFactory;
import com.rewedigital.examples.msintegration.composer.session.SessionRoot;
import com.spotify.apollo.RequestContext;
import com.spotify.apollo.Response;
import com.spotify.apollo.Status;

import okio.ByteString;

public class ComposingRequestHandler {

    private final BackendRouting routing;
    private final RouteTypes routeTypes;
    private final SessionHandler sessionHandler;

    public ComposingRequestHandler(final BackendRouting routing, final RouteTypes routeTypes,
        final SessionHandlerFactory sessionHandlerFactory) {
        this.routing = Objects.requireNonNull(routing);
        this.routeTypes = Objects.requireNonNull(routeTypes);
        this.sessionHandler = sessionHandlerFactory.build();
    }

    public CompletionStage<Response<ByteString>> execute(final RequestContext context) {
        return sessionHandler.initialize(context).thenCompose(session -> {
            return routing.matches(context.request(), session)
                .map(rm -> rm.routeType(routeTypes)
                    .execute(rm, context, session))
                .orElse(defaultResponse(session))
                .thenApply(sessionHandler::store);
        });
    }

    private static CompletableFuture<ResponseWithSession<ByteString>> defaultResponse(final SessionRoot session) {
        final Response<ByteString> response =
            Response.of(Status.INTERNAL_SERVER_ERROR, ByteString.encodeUtf8("Ohh.. noose!"));
        return CompletableFuture
            .completedFuture(new ResponseWithSession<ByteString>(response, session));
    }
}
