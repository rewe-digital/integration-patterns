package com.rewedigital.examples.msintegration.composer.composing.parser;

import org.attoparser.IMarkupParser;
import org.attoparser.MarkupParser;
import org.attoparser.config.ParseConfiguration;
import org.attoparser.config.ParseConfiguration.ElementBalancing;

public class Parser {

    public static final IMarkupParser PARSER = new MarkupParser(parserConfig());

    private static ParseConfiguration parserConfig() {
        final ParseConfiguration htmlConfiguration = ParseConfiguration.htmlConfiguration();
        htmlConfiguration.setElementBalancing(ElementBalancing.NO_BALANCING);
        return htmlConfiguration;
    }

}
