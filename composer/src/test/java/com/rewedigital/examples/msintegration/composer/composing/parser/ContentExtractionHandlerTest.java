package com.rewedigital.examples.msintegration.composer.composing.parser;

import static org.assertj.core.api.Assertions.assertThat;

import org.attoparser.ParseException;
import org.junit.Test;

public class ContentExtractionHandlerTest {

    @Test
    public void extractsContentFromMarkup() throws ParseException {
        final String content = Parser.PARSER.parseContent("<rewe-digital-content>test</rewe-digital-content>");
        assertThat(content).isEqualTo("test");
    }

}
