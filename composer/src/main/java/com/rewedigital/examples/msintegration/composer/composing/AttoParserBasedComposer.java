package com.rewedigital.examples.msintegration.composer.composing;

import static java.util.Objects.requireNonNull;

import java.util.concurrent.CompletableFuture;

import com.rewedigital.examples.msintegration.composer.parser.Parser;
import com.spotify.apollo.Response;

public class AttoParserBasedComposer implements ContentComposer, TemplateComposer {

    private final ContentFetcher contentFetcher;

    public AttoParserBasedComposer(final ContentFetcher contentFetcher) {
        this.contentFetcher = requireNonNull(contentFetcher);
    }

    @Override
    public CompletableFuture<Response<String>> composeTemplate(final Response<String> templateResponse) {
        final String responseBody = bodyOf(templateResponse);
        return parse(responseBody, ContentRange.allUpToo(responseBody.length()))
            .composeIncludes(contentFetcher, this)
            .thenApply(c -> c.toResponse());
    }

    @Override
    public CompletableFuture<Composition> composeContent(final Response<String> templateResponse) {
        return parse(bodyOf(templateResponse), ContentRange.empty())
            .composeIncludes(contentFetcher, this);
    }

    private IncludeProcessor parse(final String template, final ContentRange defaultContentRange) {
        final IncludeMarkupHandler includeHandler = new IncludeMarkupHandler(defaultContentRange);
        Parser.PARSER.parse(template, includeHandler);
        return includeHandler.buildProcessor(template);
    }

    private static String bodyOf(final Response<String> templateResponse) {
        return templateResponse.payload().orElse("");
    }
}
