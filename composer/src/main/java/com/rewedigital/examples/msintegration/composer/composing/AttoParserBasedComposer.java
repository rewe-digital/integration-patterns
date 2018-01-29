package com.rewedigital.examples.msintegration.composer.composing;

import static java.util.Objects.requireNonNull;

import java.util.concurrent.CompletableFuture;

import com.rewedigital.examples.msintegration.composer.session.ResponseWithSession;
import com.rewedigital.examples.msintegration.composer.session.Session;
import com.spotify.apollo.Response;

public class AttoParserBasedComposer implements ContentComposer, TemplateComposer {

    private final ContentFetcher contentFetcher;
    private final ComposerHtmlConfiguration configuration;
    private final Session session;

    public AttoParserBasedComposer(final ContentFetcher contentFetcher, final Session session,
        final ComposerHtmlConfiguration configuration) {
        this.configuration = configuration;
        this.session = session;
        this.contentFetcher = requireNonNull(contentFetcher);
    }

    @Override
    public CompletableFuture<ResponseWithSession<String>> composeTemplate(final Response<String> templateResponse) {
        return parse(bodyOf(templateResponse), ContentRange.allOf(bodyOf(templateResponse)))
            .composeIncludes(contentFetcher, this)
            .thenApply(c -> c.withSession(session.mergeWith(Session.of(templateResponse))))
            .thenApply(c -> c.toResponse());
    }

    @Override
    public CompletableFuture<Composition> composeContent(final Response<String> contentResponse) {
        return parse(bodyOf(contentResponse), ContentRange.empty())
            .composeIncludes(contentFetcher, this)
            .thenApply(c -> c.withSession(Session.of(contentResponse)));
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
