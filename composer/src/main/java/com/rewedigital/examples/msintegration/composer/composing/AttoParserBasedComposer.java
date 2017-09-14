package com.rewedigital.examples.msintegration.composer.composing;

import static java.util.Objects.requireNonNull;

import java.util.concurrent.CompletableFuture;

import com.spotify.apollo.Response;

public class AttoParserBasedComposer implements ContentComposer, TemplateComposer {

    private final ContentFetcher contentFetcher;

    public AttoParserBasedComposer(final ContentFetcher contentFetcher) {
        this.contentFetcher = requireNonNull(contentFetcher);
    }

    @Override
    public CompletableFuture<Response<String>> composeTemplate(final Response<String> templateResponse) {
        return parse(bodyOf(templateResponse), ContentRange.allOf(bodyOf(templateResponse)))
            .composeIncludes()
            .thenApply(c -> c.toResponse());
    }

    @Override
    public CompletableFuture<Composition> composeContent(final Response<String> templateResponse) {
        return parse(bodyOf(templateResponse), ContentRange.empty())
            .composeIncludes();
    }

    private IncludeHandler parse(final String template, final ContentRange defaultContentRange) {
        final IncludeHandler includeHandler = new IncludeHandler(contentFetcher, this, defaultContentRange, template);
        Parser.PARSER.parse(template, includeHandler);
        return includeHandler;
    }

    private static String bodyOf(Response<String> templateResponse) {
        return templateResponse.payload().orElse("");
    }
}
