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

import org.apache.commons.lang.NotImplementedException;

import com.rewedigital.examples.msintegration.composer.composing.parser.IncludedService;
import com.rewedigital.examples.msintegration.composer.composing.parser.IncludedService.WithContent;

public class OngoingComposition
    implements Collector<IncludedService.WithContent, OngoingComposition, OngoingComposition> {

    private final StringWriter writer;
    private final String template;
    private final List<String> assetLinks;
    private int currentIndex;
    private String result;

    public OngoingComposition(final String template) {
        this.template = template;
        this.writer = new StringWriter(template.length());
        this.assetLinks = new LinkedList<>();
        this.currentIndex = 0;
    }

    public FinishedComposition result() {
        if (result == null) {
            finish();
        }
        return new FinishedComposition(result, assetLinks);
    }

    @Override
    public BiConsumer<OngoingComposition, IncludedService.WithContent> accumulator() {
        return (r, c) -> r.write(c);
    }

    @Override
    public Function<OngoingComposition, OngoingComposition> finisher() {
        return r -> r.finish();
    }

    @Override
    public BinaryOperator<OngoingComposition> combiner() {
        return (r, s) -> {
            throw new NotImplementedException("Must not be used with parallel streams");
        };
    }

    @Override
    public Supplier<OngoingComposition> supplier() {
        return () -> this;
    }

    @Override
    public Set<Characteristics> characteristics() {
        return Collections.unmodifiableSet(new HashSet<>());
    }

    private void write(final WithContent c) {
        writer.write(template, currentIndex, c.startOffset() - currentIndex);
        writer.write(c.content());
        currentIndex = c.endOffset();
        assetLinks.addAll(c.assets());
    }

    private OngoingComposition finish() {
        writeFinalChunk();
        return this;
    }

    private void writeFinalChunk() {
        writer.write(template, currentIndex, template.length() - currentIndex);
        this.result = writer.toString();
    }
}
