package com.rewedigital.examples.msintegration.composer.composing;

import static java.util.Objects.requireNonNull;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.damnhandy.uri.template.UriTemplate;
import com.rewedigital.examples.msintegration.composer.session.SessionRoot;
import com.spotify.apollo.Client;
import com.spotify.apollo.Request;
import com.spotify.apollo.Response;

import okio.ByteString;

public class ValidatingContentFetcher implements ContentFetcher {

    private static final Logger LOGGER = LoggerFactory.getLogger(ValidatingContentFetcher.class);

    private final Client client;
    private final Map<String, Object> parsedPathArguments;
    private final SessionRoot session;

    public ValidatingContentFetcher(final Client client, final Map<String, Object> parsedPathArguments,
        final SessionRoot session) {
        this.session = session;
        this.client = requireNonNull(client);
        this.parsedPathArguments = requireNonNull(parsedPathArguments);
    }

    @Override
    public CompletableFuture<Response<String>> fetch(final String path, final String fallback) {
        if (path == null || path.trim().isEmpty()) {
            LOGGER.warn("Empty path attribute in include found");
            return CompletableFuture.completedFuture(Response.forPayload(""));
        }

        final String expandedPath = UriTemplate.fromTemplate(path).expand(parsedPathArguments);
        return client.send(session.enrich(Request.forUri(expandedPath, "GET")))
            .thenApply(this::acceptHtmlOnly)
            .thenApply(r -> toStringPayload(r, fallback))
            .toCompletableFuture();
    }

    private Response<String> toStringPayload(final Response<ByteString> response, final String fallback) {
        final String value = response.payload().map(p -> p.utf8()).orElse(fallback);
        return response.withPayload(value);
    }

    private Response<ByteString> acceptHtmlOnly(final Response<ByteString> response) {
        final String contentType = response.header("Content-Type").orElse("other");
        if (contentType != null && contentType.contains("text/html")) {
            return response;
        }
        return response.withPayload(null);
    }
}
