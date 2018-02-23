package com.rewedigital.examples.msintegration.composer.composing;

import static java.util.Objects.requireNonNull;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;

import com.rewedigital.examples.msintegration.composer.parser.Parser;
import com.rewedigital.examples.msintegration.composer.session.ResponseWithSession;
import com.rewedigital.examples.msintegration.composer.session.SessionFragment;
import com.rewedigital.examples.msintegration.composer.session.SessionRoot;
import com.spotify.apollo.Response;

public class AttoParserBasedComposer implements ContentComposer, TemplateComposer {

    private final ContentFetcher contentFetcher;
    private final ComposerHtmlConfiguration configuration;
    private final SessionRoot session;

    public AttoParserBasedComposer(final ContentFetcher contentFetcher, final SessionRoot session,
        final ComposerHtmlConfiguration configuration) {
        this.configuration = requireNonNull(configuration);
        this.contentFetcher = requireNonNull(contentFetcher);
        this.session = requireNonNull(session);
    }

    @Override
    public CompletableFuture<ResponseWithSession<String>> composeTemplate(final Response<String> templateResponse) {
        return parse(bodyOf(templateResponse), ContentRange.allUpToo(bodyOf(templateResponse).length()))
            .composeIncludes(contentFetcher, this)
            .thenApply(c -> c.withSession(SessionFragment.of(templateResponse)))
            .thenApply(c -> c.map(toResponse()));
    }

    private BiFunction<String, SessionFragment, ResponseWithSession<String>> toResponse() {
        return (payload, sessionFragment) -> new ResponseWithSession<String>(Response.forPayload(payload),
            session.mergedWith(sessionFragment));
    }

    @Override
    public CompletableFuture<Composition> composeContent(final Response<String> contentResponse) {
        return parse(bodyOf(contentResponse), ContentRange.empty())
            .composeIncludes(contentFetcher, this)
            .thenApply(c -> c.withSession(SessionFragment.of(contentResponse)));
    }

    private IncludeProcessor parse(final String template, final ContentRange defaultContentRange) {
        final IncludeMarkupHandler includeHandler = new IncludeMarkupHandler(defaultContentRange, configuration);
        Parser.PARSER.parse(template, includeHandler);
        return includeHandler.buildProcessor(template);
    }

    private static String bodyOf(final Response<String> templateResponse) {
        return templateResponse.payload().orElse("");
    }
}
