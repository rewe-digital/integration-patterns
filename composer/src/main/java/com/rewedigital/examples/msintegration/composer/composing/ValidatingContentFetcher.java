package com.rewedigital.examples.msintegration.composer.composing;

import static java.util.Objects.requireNonNull;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.damnhandy.uri.template.UriTemplate;
import com.spotify.apollo.Client;
import com.spotify.apollo.Request;
import com.spotify.apollo.Response;

import okio.ByteString;

public class ValidatingContentFetcher implements ContentFetcher {

    private static final Logger LOGGER = LoggerFactory.getLogger(ValidatingContentFetcher.class);

    private final Client client;
    private final Map<String, Object> parsedPathArguments;

    public ValidatingContentFetcher(final Client client, final Map<String, Object> parsedPathArguments) {
        this.client = requireNonNull(client);
        this.parsedPathArguments = requireNonNull(parsedPathArguments);
    }

    @Override
    public CompletableFuture<Response<String>> fetch(final String path) {
        if (path == null || path.trim().isEmpty()) {
            LOGGER.warn("Empty path attribute in include found");
            return CompletableFuture.completedFuture(Response.forPayload(""));
        }

        final String expandedPath = UriTemplate.fromTemplate(path).expand(parsedPathArguments);
        return client.send(Request.forUri(expandedPath, "GET"))
            .thenApply(this::toStringPayload)
            .toCompletableFuture();
    }

    private Response<String> toStringPayload(final Response<ByteString> response) {
        return response.withPayload(response.payload().map(p -> p.utf8()).orElse(""));
    }
}
