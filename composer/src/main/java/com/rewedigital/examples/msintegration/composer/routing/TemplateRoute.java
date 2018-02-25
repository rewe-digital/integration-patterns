package com.rewedigital.examples.msintegration.composer.routing;

import static java.util.stream.Collectors.toMap;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import com.rewedigital.examples.msintegration.composer.composing.ComposerFactory;
import com.rewedigital.examples.msintegration.composer.session.ResponseWithSession;
import com.rewedigital.examples.msintegration.composer.session.SessionRoot;
import com.spotify.apollo.Client;
import com.spotify.apollo.RequestContext;
import com.spotify.apollo.Response;
import com.spotify.apollo.Status;

import okio.ByteString;

public class TemplateRoute implements RouteType {

    private final ComposerFactory composerFactory;
    private final SessionAwareProxyClient templateClient;

    public TemplateRoute(final SessionAwareProxyClient templateClient, final ComposerFactory composerFactory) {
        this.templateClient = Objects.requireNonNull(templateClient);
        this.composerFactory = Objects.requireNonNull(composerFactory);
    }

    @Override
    public CompletionStage<ResponseWithSession<ByteString>> execute(final RouteMatch rm, final RequestContext context,
        final SessionRoot session) {
        return templateClient.fetch(rm.expandedPath(), context, session)
            .thenCompose(
                templateResponse -> process(context.requestScopedClient(), rm.parsedPathArguments(), templateResponse));
    }

    private CompletionStage<ResponseWithSession<ByteString>> process(final Client client,
        final Map<String, Object> pathArguments, final ResponseWithSession<ByteString> responseWithSession) {
        final Response<ByteString> response = responseWithSession.response();

        if (isError(response)) {
            return CompletableFuture
                .completedFuture(responseWithSession.transform(
                    r -> Response.of(Status.INTERNAL_SERVER_ERROR, ByteString.encodeUtf8("Ohh.. noose!"))));
        }

        return composerFactory
            .build(client, pathArguments, responseWithSession.session())
            .composeTemplate(response.withPayload(response.payload().get().utf8()))
            .thenApply(r -> r.transform(this::toByteString))
            .thenApply(r -> r.transform(p -> p.withHeaders(contentTypeOf(response))));
    }

    private Response<ByteString> toByteString(final Response<String> response) {
        return response.withPayload(response.payload().map(ByteString::encodeUtf8).orElse(ByteString.EMPTY));
    }

    private Map<String, String> contentTypeOf(final Response<ByteString> response) {
        return response.headerEntries().stream().filter(h -> "content-type".equalsIgnoreCase(h.getKey()))
            .collect(toMap(Entry::getKey, Entry::getValue, (a, b) -> a));
    }

    private boolean isError(final Response<ByteString> response) {
        return response.status().code() != Status.OK.code() || !response.payload().isPresent();
    }
}
