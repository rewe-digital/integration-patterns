package com.rewedigital.examples.msintegration.composer.composing;

import org.attoparser.IMarkupHandler;
import org.attoparser.IMarkupParser;
import org.attoparser.MarkupParser;
import org.attoparser.ParseException;
import org.attoparser.config.ParseConfiguration;
import org.attoparser.config.ParseConfiguration.ElementBalancing;

import com.google.common.base.Throwables;


class Parser {

    private static final IMarkupParser _PARSER = new MarkupParser(parserConfig());
    public static final Parser PARSER = new Parser();

    private static ParseConfiguration parserConfig() {
        final ParseConfiguration htmlConfiguration = ParseConfiguration.htmlConfiguration();
        htmlConfiguration.setElementBalancing(ElementBalancing.NO_BALANCING);
        return htmlConfiguration;
    }


    public void parse(final String template, final IMarkupHandler markupHandler) {
        try {
            _PARSER.parse(template, markupHandler);
        } catch (final ParseException e) {
            Throwables.propagate(e);
        }
    }
}
