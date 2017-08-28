package com.rewedigital.composer.proxy;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rewedigital.composer.Application;
import com.rewedigital.composer.routing.BackendRouting;
import com.spotify.apollo.Request;
import com.spotify.apollo.RequestContext;
import com.spotify.apollo.Response;
import com.spotify.apollo.Status;
import okio.ByteString;

public class HostRoutingHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);
    private final BackendRouting routing;

    public HostRoutingHandler(final BackendRouting routing) {
        this.routing = Objects.requireNonNull(routing);
    }

    public CompletionStage<Response<String>> execute(final RequestContext context) {
        final Request request = context.request();

        logRequestSpecification(context);

        // Find the route to the service that provides the master template.
        final Optional<URI> lookup = routing.lookup(request);

        // Fetch the template
        final Optional<CompletionStage<Response<ByteString>>> templateRequest =
            lookup.map(uri -> request(context, request, uri));

        // Parse

        // Start the minions

        // Join the minions

        // Compose

        return templateRequest.map(tr -> tr.thenApply(this::composedResponse)).orElseGet(this::defaultResponse);
    }

    private void logRequestSpecification(final RequestContext context) {
        final String path = context.pathArgs().get("path");
        final List<Entry<String, String>> headers = context.request().headerEntries();
        final Map<String, List<String>> params = context.request().parameters();
        LOGGER.debug("Serving request for path: {} with params {} and headers {}.", path, params, headers);
    }

    private CompletionStage<Response<ByteString>> request(
        final RequestContext context, final Request request, final URI uri) {
        return context.requestScopedClient().send(Request.forUri(uri.toString(), request.method()));
    }

    private Response<String> composedResponse(final Response<ByteString> response) {
        if (response.status().code() != Status.OK.code()) {
            // Do whatever suits your environment, retrieve the data from a cache, re-execute the
            // request or just fail.
            return Response.forStatus(Status.IM_A_TEAPOT);
        }
        return Response.forPayload(response.payload().get().utf8());
    }

    private CompletableFuture<Response<String>> defaultResponse() {
        return CompletableFuture.completedFuture(Response.forPayload("Ohh.. noose!"));
    }
}
