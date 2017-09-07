package com.rewedigital.examples.msintegration.composer.proxy;

import static com.spotify.apollo.Response.forPayload;
import static java.util.stream.Collectors.toMap;
import static okio.ByteString.encodeUtf8;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rewedigital.examples.msintegration.composer.composing.ComposerFactory;
import com.rewedigital.examples.msintegration.composer.routing.BackendRouting;
import com.rewedigital.examples.msintegration.composer.routing.BackendRouting.RouteMatch;
import com.spotify.apollo.Request;
import com.spotify.apollo.RequestContext;
import com.spotify.apollo.Response;
import com.spotify.apollo.Status;

import okio.ByteString;

public class ComposingRequestHandler {

    private static final CompletableFuture<Response<ByteString>> ERROR_PAGE = CompletableFuture
        .completedFuture(Response.of(Status.NOT_FOUND, ByteString.encodeUtf8("Ohh.. noose!")));

    private static final Logger LOGGER = LoggerFactory.getLogger(ComposingRequestHandler.class);

    private final BackendRouting routing;
    private final TemplateClient templateClient;
    private final ComposerFactory composerFactory;

    public ComposingRequestHandler(final BackendRouting routing, final TemplateClient templateClient,
        final ComposerFactory composerFactory) {
        this.routing = Objects.requireNonNull(routing);
        this.templateClient = Objects.requireNonNull(templateClient);
        this.composerFactory = Objects.requireNonNull(composerFactory);
    }

    public CompletionStage<Response<ByteString>> execute(final RequestContext context) {
        final Request request = context.request();
        final Optional<RouteMatch> match = routing.matches(request);
        return match.map(rm -> {
            LOGGER.info("The request {} matched the backend route {}.", request.uri(), match);
            return templateClient.getTemplate(rm, request, context).thenCompose(r -> compose(context, rm, r));
        }).orElse(defaultResponse());
    }

    private CompletionStage<Response<ByteString>> compose(final RequestContext context, final RouteMatch match,
        final Response<ByteString> response) {
        if (match.shouldProxy()) {
            return CompletableFuture.completedFuture(response);
        }

        if (response.status().code() != Status.OK.code() || !response.payload().isPresent()) {
            // Do whatever suits your environment, retrieve the data from a cache,
            // re-execute the request or just fail.
            return defaultResponse();
        }

        final String responseAsUtf8 = response.payload().get().utf8();
        return composerFactory.build(context.requestScopedClient()).compose(responseAsUtf8)
            .thenApply(c -> c.body())
            .thenApply(r -> forPayload(encodeUtf8(r))
                .withHeaders(transformHeaders(response.headerEntries())));
    }

    private Map<String, String> transformHeaders(final List<Entry<String, String>> headerEntries) {
        return headerEntries.stream().filter(h -> "content-type".equalsIgnoreCase(h.getKey()))
            .collect(toMap(Entry::getKey, Entry::getValue, (a, b) -> a));
    }

    private static CompletableFuture<Response<ByteString>> defaultResponse() {
        return ERROR_PAGE;
    }
}
