package com.rewedigital.examples.msintegration.composer.composing;

import org.attoparser.IMarkupHandler;

public class AllContentMarkupHandler extends ContentMarkupHandler {

    private final int contentEnd;

    public AllContentMarkupHandler(final IMarkupHandler next, final int contentEnd) {
        super(next);
        this.contentEnd = contentEnd;
    }

    public ContentRange contentRange() {
        return new ContentRange(0, contentEnd);
    }
}
