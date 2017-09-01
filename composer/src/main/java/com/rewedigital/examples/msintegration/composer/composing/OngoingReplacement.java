package com.rewedigital.examples.msintegration.composer.composing;

import java.io.StringWriter;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.apache.commons.lang.NotImplementedException;

import com.rewedigital.examples.msintegration.composer.composing.parser.IncludedService;
import com.rewedigital.examples.msintegration.composer.composing.parser.IncludedService.WithContent;

public class OngoingReplacement
    implements Collector<IncludedService.WithContent, OngoingReplacement, OngoingReplacement> {

    private final StringWriter writer;
    private final String template;
    private final List<String> assetLinks;
    private int currentIndex;
    private String result;

    public OngoingReplacement(final String template) {
        this.template = template;
        this.writer = new StringWriter(template.length());
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
