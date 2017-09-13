package com.rewedigital.examples.msintegration.composer.composingold;

import static com.rewedigital.examples.msintegration.composer.composingold.parser.Parser.PARSER;
import static com.rewedigital.examples.msintegration.composer.util.StreamUtil.flatten;
import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import com.rewedigital.examples.msintegration.composer.composingold.parser.Composer;
import com.rewedigital.examples.msintegration.composer.composingold.parser.Composition;
import com.rewedigital.examples.msintegration.composer.composingold.parser.IncludedService;
import com.rewedigital.examples.msintegration.composer.composingold.parser.IncludedService.WithComposition;
import com.spotify.apollo.Client;
import com.spotify.apollo.Response;

public class TemplateComposer implements Composer {

    private final Client client;
    private final Map<String, Object> parsedPathArguments;

    public TemplateComposer(final Client client, final Map<String, Object> parsedPathArguments) {
        this.client = requireNonNull(client);
        this.parsedPathArguments = requireNonNull(parsedPathArguments);
    }

    public CompletableFuture<Composition> compose(final Response<String> response) {
        final String template = response.payload().orElse("");
        final List<IncludedService> includes = parseIncludes(template);
        final CompletableFuture<Stream<WithComposition>> futureContentStream = fetchContent(includes);
        return replaceInTemplate(template, futureContentStream);
    }

    private List<IncludedService> parseIncludes(final String template) {
        return PARSER.parseIncludes(template);
    }

    private CompletableFuture<Stream<IncludedService.WithComposition>> fetchContent(
        final List<IncludedService> includes) {
        return flatten(streamOfFutureContent(includes));
    }

    private Stream<CompletableFuture<WithComposition>> streamOfFutureContent(final List<IncludedService> includes) {
        return includes.stream()
            .map(include -> include
                .fetchContent(client, parsedPathArguments))
            .map(futureResponse -> futureResponse
                .thenCompose(response -> response.extractContent(this)));
    }

    private CompletableFuture<Composition> replaceInTemplate(final String template,
        final CompletableFuture<Stream<IncludedService.WithComposition>> futureContentStream) {
        return futureContentStream
            .thenApply(contentStream -> contentStream
                .collect(new OngoingComposition(template))
                .result());
    }

}