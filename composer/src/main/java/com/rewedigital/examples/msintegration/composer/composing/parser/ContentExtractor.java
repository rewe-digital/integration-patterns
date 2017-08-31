package com.rewedigital.examples.msintegration.composer.composing.parser;

import static com.rewedigital.examples.msintegration.composer.composing.parser.Parser.PARSER;

import org.attoparser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Throwables;
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
        final ContentExtractionHandler handler = new ContentExtractionHandler();
        try {
            PARSER.parse(response.payload().get().utf8(), handler);
        } catch (final ParseException e) {
            Throwables.propagate(e);
        }
        final Content result = new Content();
        result.body(handler.content());
        response.header(STYLESHEET_HEADER)
                .map(href -> buildCssLink(href))
                .ifPresent(link -> result.addAssetLink(link));
        return result;
    }

    private String buildCssLink(final String href) {
        return String.format("<link rel=\"stylesheet\" href=\"%s\" />", href);
    }

}
