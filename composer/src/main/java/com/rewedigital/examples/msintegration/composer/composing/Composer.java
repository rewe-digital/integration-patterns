package com.rewedigital.examples.msintegration.composer.composing;

import static com.rewedigital.examples.msintegration.composer.composing.parser.Parser.PARSER;
import static com.rewedigital.examples.msintegration.composer.util.StreamUtil.flatten;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletionStage;
import java.util.stream.Stream;

import com.rewedigital.examples.msintegration.composer.composing.parser.Content;
import com.rewedigital.examples.msintegration.composer.composing.parser.ContentExtractor;
import com.rewedigital.examples.msintegration.composer.composing.parser.IncludedService;
import com.rewedigital.examples.msintegration.composer.composing.parser.IncludedService.WithContent;
import com.spotify.apollo.Environment;

public class Composer implements ContentExtractor.Composer {

    private final ContentExtractor contentExtractor;
    private final Environment environment;

    public Composer(final Environment environment) {
        this.environment = Objects.requireNonNull(environment);
        this.contentExtractor = new ContentExtractor(this);
    }

    public CompletionStage<Content> compose(final String template) {
        final List<IncludedService> includes = parseIncludes(template);
        final CompletionStage<Stream<WithContent>> futureContentStream = fetchContent(includes);
        return replaceInTemplate(template, futureContentStream);
    }

    private List<IncludedService> parseIncludes(final String template) {
        return PARSER.parseIncludes(template);
    }

    private CompletionStage<Stream<IncludedService.WithContent>> fetchContent(final List<IncludedService> includes) {
        return flatten(streamOfFutureContent(includes));
    }

    private Stream<CompletionStage<WithContent>> streamOfFutureContent(final List<IncludedService> includes) {
        return includes.stream().map(include -> include.fetchContent(environment.client())).map(
            futureResponse -> futureResponse.thenCompose(response -> response.extractContent(contentExtractor)));
    }

    private CompletionStage<Content> replaceInTemplate(final String template,
        final CompletionStage<Stream<IncludedService.WithContent>> futureContentStream) {
        return futureContentStream
            .thenApply(contentStream -> contentStream.collect(new OngoingComposition(template)).result());
    }

}
