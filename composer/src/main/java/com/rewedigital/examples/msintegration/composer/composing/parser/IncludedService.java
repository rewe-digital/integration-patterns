package com.rewedigital.examples.msintegration.composer.composing.parser;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletionStage;

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
 * <li>{@link WithContent#startOffset()}, {@link WithContent#endOffset()} return the position in the original template
 * of the element that should be replaced with {@link WithContent#content()}</li>
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
        public CompletionStage<IncludedService.WithContent> extractContent(final ContentExtractor contentExtractor) {
            final CompletionStage<Content> content = contentExtractor.contentFrom(response, path);
            return content.thenApply(c -> new WithContent(c, startOffset, endOffset));
        }
    }

    /**
     * Enhances the included service with the content from the response of the fetch call.
     */
    public static class WithContent {

        private final Content content;
        private final int startOffset;
        private final int endOffset;

        public WithContent(final Content content, final int startOffset, final int endOffset) {
            this.content = content;
            this.startOffset = startOffset;
            this.endOffset = endOffset;
        }

        public int startOffset() {
            return startOffset;
        }

        public int endOffset() {
            return endOffset;
        }

        public Content content() {
            return content;
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
