package com.rewedigital.examples.msintegration.composer.composing.parser;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.attoparser.AbstractMarkupHandler;
import org.attoparser.ParseException;
import org.attoparser.util.TextUtil;

public class AssetsExtractionHandler extends AbstractMarkupHandler {

    private boolean parsingHead = false;
    private boolean parsingLink = false;

    private final List<String> links = new LinkedList<>();
    private Map<String, String> attributes;

    public List<String> links() {
        return links;
    }

    @Override
    public void handleStandaloneElementStart(char[] buffer, int nameOffset, int nameLen, boolean minimized, int line,
        int col) throws ParseException {
        super.handleStandaloneElementStart(buffer, nameOffset, nameLen, minimized, line, col);
        if (parsingLink) {
            // next element after standalone element
            pushLink();
        }

        if (parsingHead && isLinkElement(buffer, nameOffset, nameLen)) {
            parsingLink = true;
            attributes = new HashMap<>();
        }
    }

    @Override
    public void handleOpenElementStart(final char[] buffer, final int nameOffset, final int nameLen, final int line,
        final int col) throws ParseException {
        super.handleOpenElementStart(buffer, nameOffset, nameLen, line, col);
        if (parsingLink) {
            // next element after standalone element
            pushLink();
        }

        if (isHeadElement(buffer, nameOffset, nameLen)) {
            parsingHead = true;
        } else if (parsingHead && isLinkElement(buffer, nameOffset, nameLen)) {
            parsingLink = true;
            attributes = new HashMap<>();
        }

    }

    @Override
    public void handleCloseElementEnd(final char[] buffer, final int nameOffset, final int nameLen, final int line,
        final int col) throws ParseException {
        super.handleCloseElementEnd(buffer, nameOffset, nameLen, line, col);
        if (isHeadElement(buffer, nameOffset, nameLen)) {
            parsingHead = false;
        } else if (isLinkElement(buffer, nameOffset, nameLen)) {
            pushLink();
        }
    }


    @Override
    public void handleAttribute(char[] buffer, int nameOffset, int nameLen, int nameLine, int nameCol,
        int operatorOffset, int operatorLen, int operatorLine, int operatorCol, int valueContentOffset,
        int valueContentLen, int valueOuterOffset, int valueOuterLen, int valueLine, int valueCol)
        throws ParseException {
        super.handleAttribute(buffer, nameOffset, nameLen, nameLine, nameCol, operatorOffset, operatorLen, operatorLine,
            operatorCol, valueContentOffset, valueContentLen, valueOuterOffset, valueOuterLen, valueLine, valueCol);

        if (parsingLink) {
            attributes.put(new String(buffer, nameOffset, nameLen),
                new String(buffer, valueContentOffset, valueContentLen));
        }
    }

    private boolean isLinkElement(final char[] buffer, final int nameOffset, final int nameLen) {
        return TextUtil.contains(true, buffer, nameOffset, nameLen, "link", 0, "link".length());
    }

    private boolean isHeadElement(final char[] buffer, final int nameOffset, final int nameLen) {
        return TextUtil.contains(true, buffer, nameOffset, nameLen, "head", 0, "head".length());
    }

    private void pushLink() {
        if (attributes.getOrDefault("data-rd-options", "").contains("include")) {
            links.add(
                attributes
                    .entrySet()
                    .stream()
                    .reduce("<link ",
                        (l, e) -> l + e.getKey() + "=\"" + e.getValue() + "\" ",
                        (a, b) -> a + b)
                    + "/>");
        }
        parsingLink = false;
        attributes = null;
    }



}
