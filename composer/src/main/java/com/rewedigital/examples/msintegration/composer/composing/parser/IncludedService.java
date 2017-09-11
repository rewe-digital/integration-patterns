package com.rewedigital.examples.msintegration.composer.composing.parser;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.damnhandy.uri.template.UriTemplate;
import com.spotify.apollo.Client;
import com.spotify.apollo.Request;
import com.spotify.apollo.Response;
import com.spotify.apollo.Status;

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
    private static final Logger LOGGER = LoggerFactory.getLogger(IncludedService.class);

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
         * @param composer composer to handle nested composition
         * @return the content
         */
        public CompletionStage<IncludedService.WithComposition> extractContent(final Composer composer) {
            return compositionWith(composer)
                .thenApply(c -> new WithComposition(c, response, startOffset, endOffset));
        }

        private CompletionStage<Composition> compositionWith(final Composer composer) {
            if (response.status().code() != Status.OK.code() || !response.payload().isPresent()
                || response.payload().get().size() == 0) {
                LOGGER.warn("Missing content from {} with status {} - returning empty default", path,
                    response.status().code());
                return CompletableFuture.completedFuture(new Composition());
            }

            return composer.compose(response.withPayload(response.payload().get().utf8()));
        }
    }

    /**
     * Enhances the included service with the composition from the response of the fetch call.
     */
    public static class WithComposition {

        private final Composition composition;
        private final Response<ByteString> response;
        private final int startOffset;
        private final int endOffset;

        public WithComposition(final Composition composition, final Response<ByteString> response,
            final int startOffset, final int endOffset) {
            this.composition = composition;
            this.response = response;
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

        public Response<ByteString> response() {
            return response;
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
    public CompletionStage<WithResponse> fetchContent(final Client client,
        final Map<String, Object> parsedPathArguments) {
        final String expandedPath = UriTemplate.fromTemplate(path()).expand(parsedPathArguments);
        return client.send(Request.forUri(expandedPath, "GET"))
            .thenApply(response -> new IncludedService.WithResponse(response, expandedPath, startOffset, endOffset));
    }

}
