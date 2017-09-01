package com.rewedigital.examples.msintegration.composer.composing.parser;

import java.util.List;

import org.attoparser.IMarkupParser;
import org.attoparser.MarkupParser;
import org.attoparser.ParseException;
import org.attoparser.config.ParseConfiguration;
import org.attoparser.config.ParseConfiguration.ElementBalancing;

import com.google.common.base.Throwables;

public class Parser {

    private static final IMarkupParser _PARSER = new MarkupParser(parserConfig());
    public static final Parser PARSER = new Parser();

    private static ParseConfiguration parserConfig() {
        final ParseConfiguration htmlConfiguration = ParseConfiguration.htmlConfiguration();
        htmlConfiguration.setElementBalancing(ElementBalancing.NO_BALANCING);
        return htmlConfiguration;
    }

    public List<IncludedService> parseIncludes(final String template) {
        final ContentContributorSelectorHandler handler = new ContentContributorSelectorHandler();
        try {
            _PARSER.parse(template, handler);
        } catch (final ParseException e) {
            Throwables.propagate(e);
        }
        return handler.includedServices();
    }


    public String parseContent(final String template) {
        final ContentExtractionHandler handler = new ContentExtractionHandler();
        try {
            _PARSER.parse(template, handler);
        } catch (final ParseException e) {
            Throwables.propagate(e);
        }
        return handler.content();
    }

}
