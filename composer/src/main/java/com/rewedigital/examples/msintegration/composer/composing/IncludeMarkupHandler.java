package com.rewedigital.examples.msintegration.composer.composing;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.attoparser.AbstractMarkupHandler;
import org.attoparser.IMarkupHandler;
import org.attoparser.ParseException;
import org.attoparser.output.OutputMarkupHandler;
import org.attoparser.util.TextUtil;

class IncludeMarkupHandler extends AbstractMarkupHandler {

    private final char[] includeTag;

    private final List<IncludedService> includedServices = new ArrayList<>();
    private final ContentMarkupHandler contentMarkupHandler;

    private Optional<IncludedService> include = Optional.empty();
    private StringWriter fallbackWriter;
    private IMarkupHandler next;
    private boolean collectAttributes = false;


    public IncludeMarkupHandler(final ContentRange defaultContentRange, final ComposerHtmlConfiguration configuration) {
        this.includeTag = configuration.includeTag().toCharArray();
        contentMarkupHandler = new ContentMarkupHandler(defaultContentRange, configuration);
        next = contentMarkupHandler;
    }

    public IncludeProcessor buildProcessor(final String template) {
        return new IncludeProcessor(template, includedServices, contentRange(),
            assetLinks());
    }


    private ContentRange contentRange() {
        return contentMarkupHandler.contentRange();
    }

    private List<String> assetLinks() {
        return contentMarkupHandler.assetLinks();
    }

    @Override
    public void handleOpenElementStart(final char[] buffer, final int nameOffset, final int nameLen, final int line,
        final int col)
        throws ParseException {
        next.handleOpenElementStart(buffer, nameOffset, nameLen, line, col);
        if (isIncludeElement(buffer, nameOffset, nameLen)) {
            final IncludedService value = new IncludedService();
            value.startOffset(nameOffset - 1);
            include = Optional.of(value);
            collectAttributes = true;

            // Store the content..
            fallbackWriter = new StringWriter();
            next = new OutputMarkupHandler(fallbackWriter);
        }
    }

    @Override
    public void handleOpenElementEnd(
        final char[] buffer,
        final int nameOffset, final int nameLen,
        final int line, final int col)
        throws ParseException {
        if (isIncludeElement(buffer, nameOffset, nameLen)) {
            collectAttributes = false;
        } else {
            next.handleOpenElementEnd(buffer, nameOffset, nameLen, line, col);
        }
    }

    @Override
    public void handleAttribute(final char[] buffer, final int nameOffset, final int nameLen, final int nameLine,
        final int nameCol, final int operatorOffset, final int operatorLen, final int operatorLine,
        final int operatorCol, final int valueContentOffset, final int valueContentLen, final int valueOuterOffset,
        final int valueOuterLen, final int valueLine, final int valueCol)
        throws ParseException {

        if (collectAttributes) {
            final IncludedService includedService = include.get();
            includedService.put(new String(buffer, nameOffset, nameLen),
                new String(buffer, valueContentOffset, valueContentLen));
        } else {
            next.handleAttribute(buffer, nameOffset, nameLen, nameLine, nameCol, operatorOffset, operatorLen,
                operatorLine, operatorCol, valueContentOffset, valueContentLen, valueOuterOffset, valueOuterLen,
                valueLine, valueCol);
        }

    }

    @Override
    public void handleCloseElementStart(
        final char[] buffer,
        final int nameOffset, final int nameLen,
        final int line, final int col)
        throws ParseException {
        if (!isIncludeElement(buffer, nameOffset, nameLen)) {
            next.handleCloseElementStart(buffer, nameOffset, nameLen, line, col);
        }
    }

    @Override
    public void handleCloseElementEnd(final char[] buffer, final int nameOffset, final int nameLen, final int line,
        final int col)
        throws ParseException {
        if (isIncludeElement(buffer, nameOffset, nameLen) && include.isPresent()) {
            final IncludedService value = include.get();
            value.endOffset(nameOffset + nameLen + 1);
            value.fallback(fallbackWriter.toString());
            includedServices.add(value);
            include = Optional.empty();

            // Reset the chain
            fallbackWriter = null;
            next = contentMarkupHandler;
        }
        next.handleCloseElementEnd(buffer, nameOffset, nameLen, line, col);
    }

    private boolean isIncludeElement(final char[] buffer, final int nameOffset, final int nameLen) {
        return TextUtil.contains(true, buffer, nameOffset, nameLen, includeTag, 0, includeTag.length);
    }

    @Override
    public void handleDocumentStart(
        final long startTimeNanos, final int line, final int col)
        throws ParseException {
        next.handleDocumentStart(startTimeNanos, line, col);
    }

    @Override
    public void handleDocumentEnd(
        final long endTimeNanos, final long totalTimeNanos, final int line, final int col)
        throws ParseException {
        next.handleDocumentEnd(endTimeNanos, totalTimeNanos, line, col);
    }

    @Override
    public void handleXmlDeclaration(
        final char[] buffer,
        final int keywordOffset, final int keywordLen,
        final int keywordLine, final int keywordCol,
        final int versionOffset, final int versionLen,
        final int versionLine, final int versionCol,
        final int encodingOffset, final int encodingLen,
        final int encodingLine, final int encodingCol,
        final int standaloneOffset, final int standaloneLen,
        final int standaloneLine, final int standaloneCol,
        final int outerOffset, final int outerLen,
        final int line, final int col)
        throws ParseException {
        next.handleXmlDeclaration(
            buffer,
            keywordOffset, keywordLen, keywordLine, keywordCol,
            versionOffset, versionLen, versionLine, versionCol,
            encodingOffset, encodingLen, encodingLine, encodingCol,
            standaloneOffset, standaloneLen, standaloneLine, standaloneCol,
            outerOffset, outerLen, line, col);
    }

    @Override
    public void handleDocType(
        final char[] buffer,
        final int keywordOffset, final int keywordLen,
        final int keywordLine, final int keywordCol,
        final int elementNameOffset, final int elementNameLen,
        final int elementNameLine, final int elementNameCol,
        final int typeOffset, final int typeLen,
        final int typeLine, final int typeCol,
        final int publicIdOffset, final int publicIdLen,
        final int publicIdLine, final int publicIdCol,
        final int systemIdOffset, final int systemIdLen,
        final int systemIdLine, final int systemIdCol,
        final int internalSubsetOffset, final int internalSubsetLen,
        final int internalSubsetLine, final int internalSubsetCol,
        final int outerOffset, final int outerLen,
        final int outerLine, final int outerCol)
        throws ParseException {
        next.handleDocType(
            buffer,
            keywordOffset, keywordLen, keywordLine, keywordCol,
            elementNameOffset, elementNameLen, elementNameLine, elementNameCol,
            typeOffset, typeLen, typeLine, typeCol,
            publicIdOffset, publicIdLen, publicIdLine, publicIdCol,
            systemIdOffset, systemIdLen, systemIdLine, systemIdCol,
            internalSubsetOffset, internalSubsetLen, internalSubsetLine, internalSubsetCol,
            outerOffset, outerLen, outerLine, outerCol);
    }

    @Override
    public void handleCDATASection(
        final char[] buffer,
        final int contentOffset, final int contentLen,
        final int outerOffset, final int outerLen,
        final int line, final int col)
        throws ParseException {
        next.handleCDATASection(buffer, contentOffset, contentLen, outerOffset, outerLen, line, col);
    }

    @Override
    public void handleComment(
        final char[] buffer,
        final int contentOffset, final int contentLen,
        final int outerOffset, final int outerLen,
        final int line, final int col)
        throws ParseException {
        next.handleComment(buffer, contentOffset, contentLen, outerOffset, outerLen, line, col);
    }

    @Override
    public void handleText(
        final char[] buffer,
        final int offset, final int len,
        final int line, final int col)
        throws ParseException {
        next.handleText(buffer, offset, len, line, col);
    }

    @Override
    public void handleStandaloneElementStart(
        final char[] buffer,
        final int nameOffset, final int nameLen,
        final boolean minimized, final int line, final int col)
        throws ParseException {
        next.handleStandaloneElementStart(buffer, nameOffset, nameLen, minimized, line, col);
    }

    @Override
    public void handleStandaloneElementEnd(
        final char[] buffer,
        final int nameOffset, final int nameLen,
        final boolean minimized, final int line, final int col)
        throws ParseException {
        next.handleStandaloneElementEnd(buffer, nameOffset, nameLen, minimized, line, col);
    }

    @Override
    public void handleAutoOpenElementStart(
        final char[] buffer,
        final int nameOffset, final int nameLen,
        final int line, final int col)
        throws ParseException {
        next.handleAutoOpenElementStart(buffer, nameOffset, nameLen, line, col);
    }

    @Override
    public void handleAutoOpenElementEnd(
        final char[] buffer,
        final int nameOffset, final int nameLen,
        final int line, final int col)
        throws ParseException {
        next.handleAutoOpenElementEnd(buffer, nameOffset, nameLen, line, col);
    }


    @Override
    public void handleAutoCloseElementStart(
        final char[] buffer,
        final int nameOffset, final int nameLen,
        final int line, final int col)
        throws ParseException {
        next.handleAutoCloseElementStart(buffer, nameOffset, nameLen, line, col);
    }

    @Override
    public void handleAutoCloseElementEnd(
        final char[] buffer,
        final int nameOffset, final int nameLen,
        final int line, final int col)
        throws ParseException {
        next.handleAutoCloseElementEnd(buffer, nameOffset, nameLen, line, col);
    }

    @Override
    public void handleUnmatchedCloseElementStart(
        final char[] buffer,
        final int nameOffset, final int nameLen,
        final int line, final int col)
        throws ParseException {
        next.handleUnmatchedCloseElementStart(buffer, nameOffset, nameLen, line, col);
    }

    @Override
    public void handleUnmatchedCloseElementEnd(
        final char[] buffer,
        final int nameOffset, final int nameLen,
        final int line, final int col)
        throws ParseException {
        next.handleUnmatchedCloseElementEnd(buffer, nameOffset, nameLen, line, col);
    }

    @Override
    public void handleInnerWhiteSpace(
        final char[] buffer,
        final int offset, final int len,
        final int line, final int col)
        throws ParseException {
        next.handleInnerWhiteSpace(buffer, offset, len, line, col);
    }

    @Override
    public void handleProcessingInstruction(
        final char[] buffer,
        final int targetOffset, final int targetLen,
        final int targetLine, final int targetCol,
        final int contentOffset, final int contentLen,
        final int contentLine, final int contentCol,
        final int outerOffset, final int outerLen,
        final int line, final int col)
        throws ParseException {
        next.handleProcessingInstruction(
            buffer,
            targetOffset, targetLen, targetLine, targetCol,
            contentOffset, contentLen, contentLine, contentCol,
            outerOffset, outerLen, line, col);
    }

}
