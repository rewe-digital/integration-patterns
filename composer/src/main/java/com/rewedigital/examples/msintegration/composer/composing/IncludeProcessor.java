package com.rewedigital.examples.msintegration.composer.composing;

import static com.rewedigital.examples.msintegration.composer.util.StreamUtil.flatten;
import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.concurrent.CompletableFuture;
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

}
