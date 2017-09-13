package com.rewedigital.examples.msintegration.composer.composing;

import org.attoparser.AbstractChainedMarkupHandler;
import org.attoparser.IMarkupHandler;
import org.attoparser.ParseException;
import org.attoparser.util.TextUtil;

public class ContentMarkupHandler extends AbstractChainedMarkupHandler {

    public ContentMarkupHandler(final IMarkupHandler next) {
        super(next);
    }

    private static final char[] CONTENT_ELEMENT = "rewe-digital-content".toCharArray();
    private int contentStart = 0;
    private int contentEnd = 0;


    public ContentRange contentRange() {
        return new ContentRange(contentStart, contentEnd);
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
            contentEnd = nameOffset - 2;
        }
    }

    private boolean isContentElement(final char[] buffer, final int nameOffset, final int nameLen) {
        return contentEnd <= 0
            && TextUtil.contains(true, buffer, nameOffset, nameLen, CONTENT_ELEMENT, 0, CONTENT_ELEMENT.length);
    }
}
