package com.rewedigital.examples.msintegration.composer.composingold.parser;

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
        super.handleOpenElementStart(buffer, nameOffset, nameLen, line, col);
		if (isContentElement(buffer, nameOffset, nameLen)) {
			contentStart = nameOffset + nameLen + 1;
		}
	}

	@Override
	public void handleCloseElementEnd(final char[] buffer, final int nameOffset, final int nameLen, final int line,
			final int col) throws ParseException {
        super.handleCloseElementEnd(buffer, nameOffset, nameLen, line, col);
		if (isContentElement(buffer, nameOffset, nameLen) && contentStart >= 0) {
			content = new String(buffer, contentStart, nameOffset - contentStart - 2);
		}
	}

	private boolean isContentElement(final char[] buffer, final int nameOffset, final int nameLen) {
		return TextUtil.contains(true, buffer, nameOffset, nameLen, CONTENT_ELEMENT, 0, CONTENT_ELEMENT.length);
	}
}