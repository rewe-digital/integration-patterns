package com.rewedigital.examples.msintegration.composer.composing.parser;

import org.attoparser.IMarkupParser;
import org.attoparser.MarkupParser;
import org.attoparser.ParseException;
import org.attoparser.config.ParseConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Throwables;
import com.spotify.apollo.Response;
import com.spotify.apollo.Status;

import okio.ByteString;

public class ContentExtractor {

	private static final Logger LOGGER = LoggerFactory.getLogger(ContentExtractor.class);

	private final IMarkupParser parser = new MarkupParser(ParseConfiguration.htmlConfiguration());

	public String contentFrom(final Response<ByteString> response, final String path) {
		if (response.status().code() != Status.OK.code() || !response.payload().isPresent()) {
			LOGGER.warn("Missing content from {} with status {}- returning empty default", path,
					response.status().code());
			return "";
		}
		final ContentExtractionHandler handler = new ContentExtractionHandler();
		try {
			parser.parse(response.payload().get().utf8(), handler);
		} catch (final ParseException e) {
			Throwables.propagate(e);
		}
		return handler.content();
	}

}
