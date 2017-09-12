package com.rewedigital.examples.msintegration.composer.composing;

import static java.util.stream.Collectors.toList;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

import org.apache.commons.lang.NotImplementedException;

import com.rewedigital.examples.msintegration.composer.composing.parser.Composition;
import com.rewedigital.examples.msintegration.composer.composing.parser.IncludedService;
import com.rewedigital.examples.msintegration.composer.composing.parser.IncludedService.WithComposition;
import com.rewedigital.examples.msintegration.composer.composing.parser.Parser;

public class OngoingComposition
    implements Collector<IncludedService.WithComposition, OngoingComposition, OngoingComposition> {

    public interface Handler {

        void handle(IncludedService.WithComposition includedContent);

        void finish();

        Composition.Part result();
    }

    private final List<Handler> handler;

    public OngoingComposition(final String template) {
        this(Arrays.asList(new BodyContentCompositionHandler(template, Parser.PARSER),
            new AssetLinkCompositionHander(Parser.PARSER)));
    }

    public OngoingComposition(final List<Handler> handler) {
        this.handler = handler;
    }

    public Composition result() {
        return new Composition(handler.stream().map(Handler::result).collect(toList()));
    }

    @Override
    public BiConsumer<OngoingComposition, IncludedService.WithComposition> accumulator() {
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

    private void write(final WithComposition c) {
        handler.forEach(h -> h.handle(c));
    }

    private OngoingComposition finish() {
        handler.forEach(h -> h.finish());
        return this;
    }


}
