package com.rewedigital.examples.msintegration.composer.composing;

import static com.rewedigital.examples.msintegration.composer.util.StreamUtil.flatten;

import java.io.StringWriter;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Stream;

import org.apache.commons.lang.NotImplementedException;
import org.attoparser.IMarkupParser;
import org.attoparser.MarkupParser;
import org.attoparser.ParseException;
import org.attoparser.config.ParseConfiguration;

import com.google.common.base.Throwables;
import com.rewedigital.examples.msintegration.composer.composing.parser.ContentContributorSelectorHandler;
import com.rewedigital.examples.msintegration.composer.composing.parser.ContentExtractor;
import com.rewedigital.examples.msintegration.composer.composing.parser.IncludedService;
import com.spotify.apollo.Environment;

public class Composer {

    private final IMarkupParser parser = new MarkupParser(ParseConfiguration.htmlConfiguration());
    private final ContentExtractor contentExtractor = new ContentExtractor();
    private final Environment environment;

    public Composer(final Environment environment) {
        this.environment = environment;
    }

    public CompletableFuture<String> compose(final String template) {
        final List<IncludedService> includes = parse(template);
        final CompletableFuture<Stream<IncludedService.WithContent>> content = fetchContent(
                includes);
        return replaceInTemplate(template, content);
    }

    private List<IncludedService> parse(final String template) {
        final ContentContributorSelectorHandler handler = new ContentContributorSelectorHandler();
        try {
            parser.parse(template, handler);
        } catch (final ParseException e) {
            Throwables.propagate(e);
        }

        return handler.includedServices();
    }

    private CompletableFuture<Stream<IncludedService.WithContent>> fetchContent(final List<IncludedService> includes) {
        return flatten(includes.stream()
                .map(include -> include
                        .fetchContent(environment.client()))
                .map(futureResponse -> futureResponse
                        .thenApply(response -> response
                                .extractContent(contentExtractor))));
    }

    private CompletableFuture<String> replaceInTemplate(final String template,
            final CompletableFuture<Stream<IncludedService.WithContent>> futureContentStream) {
        return futureContentStream
                .thenApply(contentStream -> contentStream
                        .collect(new OngoingReplacement(template))
                        .result());
    }

    private static class OngoingReplacement
            implements Collector<IncludedService.WithContent, OngoingReplacement, OngoingReplacement> {

        private final StringWriter writer;
        private final String baseTemplate;

        private int currentIndex;

        public OngoingReplacement(String baseTemplate) {
            this.baseTemplate = baseTemplate;
            this.writer = new StringWriter(baseTemplate.length());
            this.currentIndex = 0;
        }

        public String result() {
            return writer.toString();
        }

        @Override
        public BiConsumer<OngoingReplacement, IncludedService.WithContent> accumulator() {
            return (r, c) -> {
                r.writer.write(r.baseTemplate, r.currentIndex, c.startOffset() - r.currentIndex);
                r.writer.write(c.content());
                r.currentIndex = c.endOffset();
            };
        }

        @Override
        public Function<OngoingReplacement, OngoingReplacement> finisher() {
            return r -> {
                r.writer.write(r.baseTemplate, r.currentIndex, r.baseTemplate.length() - r.currentIndex);
                return r;
            };
        }

        @Override
        public BinaryOperator<OngoingReplacement> combiner() {
            return (r, s) -> {
                throw new NotImplementedException("must not be used with parallel streams");
            };
        }

        @Override
        public Supplier<OngoingReplacement> supplier() {
            return () -> this;
        }

        @Override
        public Set<Characteristics> characteristics() {
            return Collections.unmodifiableSet(new HashSet<>());
        }
    }
}
