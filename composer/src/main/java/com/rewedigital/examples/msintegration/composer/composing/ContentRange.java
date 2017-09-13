package com.rewedigital.examples.msintegration.composer.composing;

public class ContentRange {

    private final int start;
    private final int end;

    public static ContentRange allOf(final String template) {
        return new ContentRange(0, template.length());
    }

    public static ContentRange empty() {
        return new ContentRange(0, 0);
    }

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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + end;
        result = prime * result + start;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ContentRange other = (ContentRange) obj;
        if (end != other.end)
            return false;
        if (start != other.start)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "ContentRange [start=" + start + ", end=" + end + "]";
    }
}
