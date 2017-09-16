package com.rewedigital.examples.msintegration.composer.composing;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import com.spotify.apollo.Response;

class IncludedService {

    public static class WithResponse {
        private final int startOffset;
        private final int endOffset;
        private final Response<String> response;

        private WithResponse(final int startOffset, final int endOffset, final Response<String> response) {
            this.startOffset = startOffset;
            this.endOffset = endOffset;
            this.response = response;
        }

        public CompletableFuture<Composition> compose(final ContentComposer contentComposer) {
            return contentComposer
                .composeContent(response)
                .thenApply(c -> c.forRange(startOffset, endOffset));
        }
    }

    private int startOffset;
    private int endOffset;
    private final Map<String, String> attributes = new HashMap<>();
    private String fallback;

    public void startOffset(final int startOffset) {
        this.startOffset = startOffset;
    }

    public void endOffset(final int endOffset) {
        this.endOffset = endOffset;
    }

    public void put(final String name, final String value) {
        attributes.put(name, value);
    }

    public void fallback(final String fallback) {
        this.fallback = fallback;
    }

    public CompletableFuture<IncludedService.WithResponse> fetch(final ContentFetcher fetcher) {
        return fetcher.fetch(path(), fallback()).thenApply(r -> new WithResponse(startOffset, endOffset, r));
    }

    private String fallback() {
        return fallback;
    }

    private String path() {
        return attributes.getOrDefault("path", "");
    }

    public boolean isInRage(final ContentRange contentRange) {
        return contentRange.isInRange(startOffset);
    }

}
