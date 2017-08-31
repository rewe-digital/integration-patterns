package com.rewedigital.examples.msintegration.composer.composing.parser;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import com.spotify.apollo.Client;
import com.spotify.apollo.Request;
import com.spotify.apollo.Response;

import okio.ByteString;

/**
 * Describes a found include tag and allows to fetch and handle the content for
 * this include. </br>
 * To use the content, the following sequence of calls can be made:
 * <ul>
 * <li>{@link #fetchContent(Client)} to start fetching the content</li>
 * <li>{@link WithResponse#extractContent()} on the result of fetchContent to
 * extract relevant content from the response</li>
 * <li>{@link WithContent#startOffset()}, {@link WithContent#endOffset()} return
 * the position in the original template of the element that should be replaced
 * with {@link WithContent#content()}</li>
 * </ul>
 */
public class IncludedService {

    private final Map<String, String> attributes = new HashMap<>();
    private int startOffset;
    private int endOffset;

    /**
     * Enhances the included service with the response from the get call to fetch
     * the content.
     */
    public class WithResponse {
        private final Response<ByteString> response;

        private WithResponse(final Response<ByteString> futureServiceResponse) {
            this.response = futureServiceResponse;
        }

        /**
         * Extracts the content of this response.
         * 
         * @param contentExtractor
         *            extracts relevant content from the response
         * @return the content
         */
        public WithContent extractContent(final ContentExtractor contentExtractor) {
            final Content content = contentExtractor.contentFrom(response, path());
            return new WithContent(content);
        }
    }

    /**
     * Enhances the included service with the content from the response of the fetch
     * call.
     */
    public class WithContent {

        private final String content;
        private final List<String> assetLinks;

        public WithContent(final Content content) {
            this.content = content.body();
            this.assetLinks = content.assetLinks();
        }

        public int startOffset() {
            return startOffset;
        }

        public int endOffset() {
            return endOffset;
        }

        public String content() {
            return content;
        }

        public Collection<String> assets() {
            return assetLinks;
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
     * @param client
     *            the client to fetch the content
     * @return a future containing the response
     */
    public CompletableFuture<WithResponse> fetchContent(final Client client) {
        return client.send(Request.forUri(this.path(), "GET"))
                .thenApply(response -> new IncludedService.WithResponse(response)).toCompletableFuture();
    }

}
