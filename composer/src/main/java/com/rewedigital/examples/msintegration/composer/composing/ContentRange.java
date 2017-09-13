package com.rewedigital.examples.msintegration.composer.composing;

public class ContentRange {

    private final int start;
    private final int end;

    public ContentRange(final int start, final int end) {
        this.start = start;
        this.end = end;
    }

    public int start() {
        return start;
    }

    public int end() {
        return end;
    }

    public boolean isInRange(final int value) {
        return start <= value && value <= end;
    }

}
