package com.rewedigital.examples.msintegration.composer.composing;

import static java.util.Objects.requireNonNull;

import java.util.concurrent.CompletableFuture;

import com.spotify.apollo.Response;

public class AttoParserBasedComposer implements ContentComposer, TemplateComposer {

    private final ContentFetcher contentFetcher;
    private final ComposerConfiguration configuration;

    public AttoParserBasedComposer(final ContentFetcher contentFetcher, final ComposerConfiguration configuration) {
        this.configuration = configuration;
        this.contentFetcher = requireNonNull(contentFetcher);
    }

    @Override
    public CompletableFuture<Response<String>> composeTemplate(final Response<String> templateResponse) {
        return parse(bodyOf(templateResponse), ContentRange.allOf(bodyOf(templateResponse)))
            .composeIncludes(contentFetcher, this)
            .thenApply(c -> c.toResponse());
    }

    @Override
    public CompletableFuture<Composition> composeContent(final Response<String> templateResponse) {
        return parse(bodyOf(templateResponse), ContentRange.empty())
            .composeIncludes(contentFetcher, this);
    }

    private IncludeProcessor parse(final String template, final ContentRange defaultContentRange) {
        final IncludeMarkupHandler includeHandler = new IncludeMarkupHandler(defaultContentRange, configuration);
        Parser.PARSER.parse(template, includeHandler);
        return includeHandler.processor(template);
    }

    private static String bodyOf(final Response<String> templateResponse) {
        return templateResponse.payload().orElse("");
    }
}
