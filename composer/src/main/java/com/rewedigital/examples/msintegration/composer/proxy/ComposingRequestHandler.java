package com.rewedigital.examples.msintegration.composer.proxy;

import static java.util.stream.Collectors.toMap;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rewedigital.examples.msintegration.composer.routing.BackendRouting;
import com.rewedigital.examples.msintegration.composer.routing.BackendRouting.RouteMatch;
import com.rewedigital.examples.msintegration.composer.session.ResponseWithSession;
import com.rewedigital.examples.msintegration.composer.session.Session;
import com.rewedigital.examples.msintegration.composer.session.SessionLifecycleFactory;
import com.rewedigital.examples.msintegration.composer.session.SessionLifecylce;
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
    private final TemplateClient templateClient;
    private final ComposerFactory composerFactory;
    private final SessionLifecycleFactory sessionLifecycleFactory;

    public ComposingRequestHandler(final BackendRouting routing, final TemplateClient templateClient,
        final ComposerFactory composerFactory, final SessionLifecycleFactory sessionLifecycleFactory) {
        this.routing = Objects.requireNonNull(routing);
        this.templateClient = Objects.requireNonNull(templateClient);
        this.composerFactory = Objects.requireNonNull(composerFactory);
        this.sessionLifecycleFactory = sessionLifecycleFactory;
    }

    public CompletionStage<Response<ByteString>> execute(final RequestContext context) {
        final Request request = context.request();
        final SessionLifecylce sessionLifecylce = sessionLifecycleFactory.build();
        final Session session = sessionLifecylce.buildSession(context);
        final Optional<RouteMatch> match = routing.matches(request, session);
        return match.map(rm -> {
            LOGGER.debug("The request {} matched the backend route {}.", request, match);
            
            return templateClient.getTemplate(rm, context, session)
                .thenCompose(templateResponse -> compose(context, rm, templateResponse, sessionLifecylce));
        }).orElse(defaultResponse());
    }

    private CompletionStage<Response<ByteString>> compose(final RequestContext context, final RouteMatch match,
        final ResponseWithSession<ByteString> responseWithSession, final SessionLifecylce sessionLifecylce) {
        final Response<ByteString> response = responseWithSession.response();
        if (match.shouldProxy()) {
            return CompletableFuture.completedFuture(response);
        }

        if (response.status().code() != Status.OK.code() || !response.payload().isPresent()) {
            // Do whatever suits your environment, retrieve the data from a cache,
            // re-execute the request or just fail.
            return defaultResponse();
        }

        return composerFactory
            .build(context.requestScopedClient(), match.parsedPathArguments(), responseWithSession.session())
            .composeTemplate(response.withPayload(response.payload().get().utf8()))
            .thenApply(r -> r.writeSessionToResponse(sessionLifecylce))
            .thenApply(r -> toByteString(r)
                .withHeaders(transformHeaders(response.headerEntries())));
    }

    private Response<ByteString> toByteString(Response<String> response) {
        return response.withPayload(response.payload().map(ByteString::encodeUtf8).orElse(ByteString.EMPTY));
    }

    private Map<String, String> transformHeaders(final List<Entry<String, String>> headerEntries) {
        return headerEntries.stream().filter(h -> "content-type".equalsIgnoreCase(h.getKey()))
            .collect(toMap(Entry::getKey, Entry::getValue, (a, b) -> a));
    }

    private static CompletableFuture<Response<ByteString>> defaultResponse() {
        return ERROR_PAGE;
    }
}
