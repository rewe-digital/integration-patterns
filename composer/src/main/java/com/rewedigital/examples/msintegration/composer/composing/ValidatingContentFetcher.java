package com.rewedigital.examples.msintegration.composer.composing;

import static java.util.Objects.requireNonNull;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import com.damnhandy.uri.template.UriTemplate;
import com.spotify.apollo.Client;
import com.spotify.apollo.Request;
import com.spotify.apollo.Response;
import com.spotify.apollo.Status;

public class ValidatingContentFetcher implements ContentFetcher {

    private final Client client;
    private final Map<String, Object> parsedPathArguments;

    public ValidatingContentFetcher(Client client, Map<String, Object> parsedPathArguments) {
        this.client = requireNonNull(client);
        this.parsedPathArguments = requireNonNull(parsedPathArguments);
    }


    @Override
    public CompletableFuture<Response<String>> fetch(String path) {
        if (path == null || path.trim().isEmpty()) {
            // LOGGER.warn("Empty path attribute in include found");
            return CompletableFuture.completedFuture(Response.forStatus(Status.OK).withPayload(""));
        }

        final String expandedPath = UriTemplate.fromTemplate(path).expand(parsedPathArguments);
        return client.send(Request.forUri(expandedPath, "GET"))
            .thenApply(response -> response.withPayload(response.payload().map(p -> p.utf8()).orElse("")))
            .toCompletableFuture();
    }
}
