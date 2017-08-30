package com.rewedigital.examples.msintegration.composer.composing.parser;

import org.attoparser.AbstractMarkupHandler;
import org.attoparser.ParseException;
import org.attoparser.util.TextUtil;

public class ContentExtractionHandler extends AbstractMarkupHandler {

	private static final char[] CONTENT_ELEMENT = "rewe-digital-content".toCharArray();
	private int contentStart = -1;
	private String content = "";

	public String content() {
		return content;
	}

	@Override
	public void handleOpenElementStart(final char[] buffer, final int nameOffset, final int nameLen, final int line,
			final int col) throws ParseException {

		if (isContentElement(buffer, nameOffset, nameLen)) {
			contentStart = nameOffset + nameLen + 1;
		}
	}

	@Override
	public void handleCloseElementEnd(final char[] buffer, final int nameOffset, final int nameLen, final int line,
			final int col) throws ParseException {
		if (isContentElement(buffer, nameOffset, nameLen) && contentStart >= 0) {
			content = new String(buffer, contentStart, nameOffset - contentStart - 2);
		}
	}

	private boolean isContentElement(char[] buffer, int nameOffset, int nameLen) {
		return TextUtil.contains(true, buffer, nameOffset, nameLen, CONTENT_ELEMENT, 0, CONTENT_ELEMENT.length);
	}
}