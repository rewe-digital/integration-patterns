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

        private WithResponse(int startOffset, int endOffset, Response<String> response) {
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
    private Map<String, String> attributes = new HashMap<>();

    public void startOffset(int startOffset) {
        this.startOffset = startOffset;
    }

    public void put(String name, String value) {
        attributes.put(name, value);
    }

    public void endOffset(int endOffset) {
        this.endOffset = endOffset;
    }

    public CompletableFuture<IncludedService.WithResponse> fetch(final ContentFetcher fetcher) {
        return fetcher.fetch(path()).thenApply(r -> new WithResponse(startOffset, endOffset, r));
    }

    private String path() {
        return attributes.getOrDefault("path", "");
    }

    public boolean isInRage(ContentRange contentRange) {
        return contentRange.isInRange(startOffset);
    }

}
