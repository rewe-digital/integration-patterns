package com.rewedigital.examples.msintegration.composer.composing.parser;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

import com.rewedigital.examples.msintegration.composer.composing.parser.Composition.Part;
import com.spotify.apollo.Client;
import com.spotify.apollo.Request;
import com.spotify.apollo.Response;

import okio.ByteString;

/**
 * Describes a found include tag and allows to fetch and handle the content for this include. </br>
 * To use the content, the following sequence of calls can be made:
 * <ul>
 * <li>{@link #fetchContent(Client)} to start fetching the content</li>
 * <li>{@link WithResponse#extractContent()} on the result of fetchContent to extract relevant content from the
 * response</li>
 * <li>{@link WithComposition#startOffset()}, {@link WithComposition#endOffset()} return the position in the original
 * template of the element that should be replaced with {@link WithComposition#composition()}</li>
 * </ul>
 */
public class IncludedService {

    private final Map<String, String> attributes = new HashMap<>();
    private int startOffset;
    private int endOffset;

    /**
     * Enhances the included service with the response from the get call to fetch the content.
     */
    public static class WithResponse {

        private final Response<ByteString> response;
        private final String path;
        private final int startOffset;
        private final int endOffset;

        private WithResponse(final Response<ByteString> serviceResponse, final String path, final int startOffset,
            final int endOffset) {
            this.response = serviceResponse;
            this.path = path;
            this.startOffset = startOffset;
            this.endOffset = endOffset;
        }

        /**
         * Extracts the content of this response.
         *
         * @param contentExtractor extracts relevant content from the response
         * @return the content
         */
        public CompletionStage<IncludedService.WithComposition> extractContent(
            final ContentExtractor contentExtractor) {
            final CompletionStage<Composition> composition = contentExtractor.compositionFrom(response, path);
            return composition.thenApply(c -> new WithComposition(c, startOffset, endOffset));
        }
    }

    /**
     * Enhances the included service with the composition from the response of the fetch call.
     */
    public static class WithComposition {

        private final Composition composition;
        private final int startOffset;
        private final int endOffset;

        public WithComposition(final Composition composition, final int startOffset, final int endOffset) {
            this.composition = composition;
            this.startOffset = startOffset;
            this.endOffset = endOffset;
        }

        public int startOffset() {
            return startOffset;
        }

        public int endOffset() {
            return endOffset;
        }

        public Composition composition() {
            return composition;
        }

        public <T extends Part> Optional<T> find(Class<T> type) {
            return composition.find(type);
        }
    }

    void put(final String attribute, final String value) {
        attributes.put(attribute, value);
    }

    void startOffset(final int startOffset) {
        this.startOffset = startOffset;
    }

    void endOffset(final int endOffset) {
        this.endOffset = endOffset;
    }

    private String path() {
        return attributes.get("path");
    }

    /**
     * Starts fetching the content for this include using the provided client.
     *
     * @param client the client to fetch the content
     * @return a future containing the response
     */
    public CompletionStage<WithResponse> fetchContent(final Client client) {
        return client.send(Request.forUri(path(), "GET"))
            .thenApply(response -> new IncludedService.WithResponse(response, path(), startOffset, endOffset));
    }

}
