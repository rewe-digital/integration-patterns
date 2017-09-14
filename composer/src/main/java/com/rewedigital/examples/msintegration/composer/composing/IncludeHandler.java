package com.rewedigital.examples.msintegration.composer.composing;

import static com.rewedigital.examples.msintegration.composer.util.StreamUtil.flatten;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import org.attoparser.AbstractChainedMarkupHandler;
import org.attoparser.ParseException;
import org.attoparser.util.TextUtil;

class IncludeHandler extends AbstractChainedMarkupHandler {

    private static final char[] INCLUDE = "rewe-digital-include".toCharArray();

    private final ContentFetcher contentFetcher;
    private final ContentComposer composer;
    private final List<IncludedService> includedServices = new ArrayList<>();
    private final String template;

    private Optional<IncludedService> include = Optional.empty();

    public IncludeHandler(final ContentFetcher contentFetcher, final ContentComposer composer,
        final ContentRange defaultContentRange, final String template) {
        super(new ContentMarkupHandler(defaultContentRange));
        this.template = template;
        this.contentFetcher = Objects.requireNonNull(contentFetcher);
        this.composer = Objects.requireNonNull(composer);
    }

    public CompletableFuture<Composition> composeIncludes() {
        Stream<CompletableFuture<Composition>> composedIncludes = includedServices.stream()
            .filter(s -> s.isInRage(contentRange()))
            .map(s -> s.fetch(contentFetcher)
                .thenCompose(r -> r.compose(composer)));
        return flatten(composedIncludes)
            .thenApply(c -> new Composition(template, contentRange(), assetLinks(), c.collect(toList())));
    }

    private ContentRange contentRange() {
        return ((ContentMarkupHandler) getNext()).contentRange();
    }

    private List<String> assetLinks() {
        return ((ContentMarkupHandler) getNext()).assetLinks();
    }

    @Override
    public void handleOpenElementStart(final char[] buffer, final int nameOffset, final int nameLen, final int line,
        final int col)
        throws ParseException {
        super.handleOpenElementStart(buffer, nameOffset, nameLen, line, col);
        if (isIncludeElement(buffer, nameOffset, nameLen)) {
            final IncludedService value = new IncludedService();
            value.startOffset(nameOffset - 1);
            include = Optional.of(value);
        }
    }

    @Override
    public void handleAttribute(final char[] buffer, final int nameOffset, final int nameLen, final int nameLine,
        final int nameCol, final int operatorOffset, final int operatorLen, final int operatorLine,
        final int operatorCol, final int valueContentOffset, final int valueContentLen, final int valueOuterOffset,
        final int valueOuterLen, final int valueLine, final int valueCol)
        throws ParseException {
        super.handleAttribute(buffer, nameOffset, nameLen, nameLine, nameCol, operatorOffset, operatorLen, operatorLine,
            operatorCol, valueContentOffset, valueContentLen, valueOuterOffset, valueOuterLen, valueLine, valueCol);
        if (include.isPresent()) {
            final IncludedService includedService = include.get();
            includedService.put(new String(buffer, nameOffset, nameLen),
                new String(buffer, valueContentOffset, valueContentLen));
        }
    }

    @Override
    public void handleCloseElementEnd(final char[] buffer, final int nameOffset, final int nameLen, final int line,
        final int col)
        throws ParseException {
        super.handleCloseElementEnd(buffer, nameOffset, nameLen, line, col);
        if (isIncludeElement(buffer, nameOffset, nameLen) && include.isPresent()) {
            final IncludedService value = include.get();
            value.endOffset(nameOffset + nameLen + 1);
            includedServices.add(value);
            include = Optional.empty();
        }
    }

    private boolean isIncludeElement(final char[] buffer, final int nameOffset, final int nameLen) {
        return TextUtil.contains(true, buffer, nameOffset, nameLen, INCLUDE, 0, INCLUDE.length);
    }
}
