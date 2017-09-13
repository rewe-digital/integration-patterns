package com.rewedigital.examples.msintegration.composer.composing;

import static java.util.Objects.requireNonNull;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import com.damnhandy.uri.template.UriTemplate;
import com.spotify.apollo.Client;
import com.spotify.apollo.Request;
import com.spotify.apollo.Response;
import com.spotify.apollo.Status;

public class AttoParserBasedComposer implements ContentComposer, TemplateComposer {

    private final Client client;
    private final Map<String, Object> parsedPathArguments;

    public AttoParserBasedComposer(final Client client, final Map<String, Object> parsedPathArguments) {
        this.client = requireNonNull(client);
        this.parsedPathArguments = requireNonNull(parsedPathArguments);
    }

    @Override
    public CompletableFuture<Response<String>> composeTemplate(final Response<String> templateResponse) {
        return parse(bodyOf(templateResponse), ContentRange.allOf(bodyOf(templateResponse))).composeIncludes()
            .thenApply(c -> c.toResponse());
    }

    @Override
    public CompletableFuture<Composition> composeContent(final Response<String> templateResponse) {
        return parse(bodyOf(templateResponse), ContentRange.empty()).composeIncludes();
    }

    private IncludeHandler parse(final String template, final ContentRange defaultContentRange) {
        final IncludeHandler includeHandler = new IncludeHandler(contentFetcher(), this, defaultContentRange, template);
        Parser.PARSER.parse(template, includeHandler);
        return includeHandler;
    }

    private static String bodyOf(Response<String> templateResponse) {
        return templateResponse.payload().orElse("");
    }

    private ContentFetcher contentFetcher() {
        return new ContentFetcher() {

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
        };
    }
}
