package com.rewedigital.examples.msintegration.composer.composing;

import static com.rewedigital.examples.msintegration.composer.composing.parser.Parser.PARSER;
import static com.rewedigital.examples.msintegration.composer.util.StreamUtil.flatten;

import java.io.StringWriter;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletionStage;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang.NotImplementedException;
import org.attoparser.ParseException;

import com.google.common.base.Throwables;
import com.rewedigital.examples.msintegration.composer.composing.parser.ContentContributorSelectorHandler;
import com.rewedigital.examples.msintegration.composer.composing.parser.ContentExtractor;
import com.rewedigital.examples.msintegration.composer.composing.parser.IncludedService;
import com.rewedigital.examples.msintegration.composer.composing.parser.IncludedService.WithContent;
import com.spotify.apollo.Environment;

public class Composer {

    private final ContentExtractor contentExtractor = new ContentExtractor();
    private final Environment environment;

    public Composer(final Environment environment) {
        this.environment = Objects.requireNonNull(environment);
    }

    public CompletionStage<String> compose(final String template) {
        final List<IncludedService> includes = parse(template);

        final CompletionStage<Stream<IncludedService.WithContent>> content =
            fetchContent(includes);

        return replaceInTemplate(template, content);
    }

    private List<IncludedService> parse(final String template) {
        final ContentContributorSelectorHandler handler = new ContentContributorSelectorHandler();
        try {
            PARSER.parse(template, handler);
        } catch (final ParseException e) {
            Throwables.propagate(e);
        }
        return handler.includedServices();
    }

    private CompletionStage<Stream<IncludedService.WithContent>> fetchContent(final List<IncludedService> includes) {
        return flatten(streamOfFutureContent(includes));
    }

    private Stream<CompletionStage<WithContent>> streamOfFutureContent(final List<IncludedService> includes) {
        return includes.stream()
            .map(include -> include
                .fetchContent(environment.client()))
            .map(futureResponse -> futureResponse
                .thenApply(response -> response
                    .extractContent(contentExtractor)));
    }

    private CompletionStage<String> replaceInTemplate(final String template,
            final CompletionStage<Stream<IncludedService.WithContent>> futureContentStream) {
        return futureContentStream
                .thenApply(contentStream -> contentStream
                        .collect(new OngoingReplacement(template))
                        .result());
    }

    private static class OngoingReplacement
            implements Collector<IncludedService.WithContent, OngoingReplacement, OngoingReplacement> {

        private final StringWriter writer;
        private final String template;
        private final List<String> assetLinks;
        private int currentIndex;
        private String result;

        public OngoingReplacement(final String baseTemplate) {
            this.template = baseTemplate;
            this.writer = new StringWriter(baseTemplate.length());
            this.assetLinks = new LinkedList<>();
            this.currentIndex = 0;
        }

        public String result() {
            if (result == null) {
                finish();
            }
            return result;
        }

        @Override
        public BiConsumer<OngoingReplacement, IncludedService.WithContent> accumulator() {
            return (r, c) -> r.write(c);
        }

        private void write(final WithContent c) {
            writer.write(template, currentIndex, c.startOffset() - currentIndex);
            writer.write(c.content());
            currentIndex = c.endOffset();
            assetLinks.addAll(c.assets());
        }

        @Override
        public Function<OngoingReplacement, OngoingReplacement> finisher() {
            return r -> r.finish();
        }

        private OngoingReplacement finish() {
            writeFinalChunk();
            writeAssets();
            return this;
        }

        private void writeFinalChunk() {
            writer.write(template, currentIndex, template.length() - currentIndex);
            this.result = writer.toString();
        }

        private void writeAssets() {
            final String assets = assetLinks.stream().collect(Collectors.joining("\n"));
            result = result.replaceFirst("</head>", assets + "\n</head>");
        }

        @Override
        public BinaryOperator<OngoingReplacement> combiner() {
            return (r, s) -> {
                throw new NotImplementedException("Must not be used with parallel streams");
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
