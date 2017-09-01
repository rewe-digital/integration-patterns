package com.rewedigital.examples.msintegration.composer.composing.parser;

import static com.rewedigital.examples.msintegration.composer.composing.parser.Parser.PARSER;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.spotify.apollo.Response;
import com.spotify.apollo.Status;

import okio.ByteString;

public class ContentExtractor {

    private static final String STYLESHEET_HEADER = "x-uic-stylesheet";

    private static final Logger LOGGER = LoggerFactory.getLogger(ContentExtractor.class);

    public Content contentFrom(final Response<ByteString> response, final String path) {
        if (response.status().code() != Status.OK.code() || !response.payload().isPresent()) {
            LOGGER.warn("Missing content from {} with status {} returning empty default", path,
                response.status().code());
            return new Content();
        }


        final Content result = new Content();
        result.body(parseContent(response));
        response.header(STYLESHEET_HEADER)
            .map(href -> buildCssLink(href))
            .ifPresent(link -> result.addAssetLink(link));
        return result;
    }

    private String parseContent(final Response<ByteString> response) {
        return PARSER.parseContent(response.payload().get().utf8());
    }

    private String buildCssLink(final String href) {
        return String.format("<link rel=\"stylesheet\" href=\"%s\" />", href);
    }

}
