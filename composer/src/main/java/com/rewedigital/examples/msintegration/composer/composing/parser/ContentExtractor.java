package com.rewedigital.examples.msintegration.composer.composing.parser;

import static com.rewedigital.examples.msintegration.composer.composing.parser.Parser.PARSER;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.spotify.apollo.Response;
import com.spotify.apollo.Status;

import okio.ByteString;

public class ContentExtractor {

    public interface Composer {
        CompletionStage<Content> compose(String template);
    }

    private static final String STYLESHEET_HEADER = "x-uic-stylesheet";
    private static final Logger LOGGER = LoggerFactory.getLogger(ContentExtractor.class);

    private final Composer composer;

    public ContentExtractor(final Composer composer) {
        this.composer = composer;
    }

    public CompletionStage<Content> contentFrom(final Response<ByteString> response, final String path) {
        if (response.status().code() != Status.OK.code() || !response.payload().isPresent()) {
            LOGGER.warn("Missing content from {} with status {} returning empty default", path,
                response.status().code());
            return CompletableFuture.completedFuture(new Content());
        }

        final String rawContent = parseContent(response);
        return composer.compose(rawContent)
            .thenApply(c -> addAssetLinks(c, response));
    }

    private String parseContent(final Response<ByteString> response) {
        return PARSER.parseContent(response.payload().get().utf8());
    }

    private Content addAssetLinks(Content content, Response<ByteString> response) {
        response.header(STYLESHEET_HEADER)
            .map(href -> buildCssLink(href))
            .ifPresent(link -> content.addAssetLink(link));
        return content;
    }

    private String buildCssLink(final String href) {
        return String.format("<link rel=\"stylesheet\" href=\"%s\" />", href);
    }
}
