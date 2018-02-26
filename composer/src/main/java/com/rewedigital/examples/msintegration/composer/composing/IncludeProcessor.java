package com.rewedigital.examples.msintegration.composer.composing;

import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class IncludeProcessor {

    private final List<IncludedService> includedServices;
    private final ContentRange contentRange;
    private final List<String> assetLinks;
    private final String template;

    public IncludeProcessor(final String template, final List<IncludedService> includedServices,
        final ContentRange contentRange, final List<String> assetLinks) {
        this.template = template;
        this.includedServices = includedServices;
        this.contentRange = contentRange;
        this.assetLinks = assetLinks;
    }

    public CompletableFuture<Composition> composeIncludes(final ContentFetcher contentFetcher,
        final ContentComposer composer) {
        final Stream<CompletableFuture<Composition>> composedIncludes = includedServices.stream()
            .filter(s -> s.isInRage(contentRange))
            .map(s -> s.fetch(contentFetcher)
                .thenCompose(r -> r.compose(composer)));
        return flatten(composedIncludes)
            .thenApply(c -> Composition.forRoot(template, contentRange, assetLinks, c.collect(toList())));
    }

    private static <T> CompletableFuture<Stream<T>> flatten(final Stream<CompletableFuture<T>> com) {
        final List<CompletableFuture<T>> list = com.map(c -> c.toCompletableFuture()).collect(Collectors.toList());
        return CompletableFuture.allOf(list.toArray(new CompletableFuture[list.size()]))
            .thenApply(v -> list.stream().map(CompletableFuture::join));
    }

}
