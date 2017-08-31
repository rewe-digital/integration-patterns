package com.rewedigital.examples.msintegration.composer.composing.parser;

import static org.assertj.core.api.Assertions.assertThat;

import org.attoparser.ParseException;
import org.junit.Test;

public class ContentExtractionHandlerTest {

    @Test
    public void extractsContentFromMarkup() throws ParseException {
        final ContentExtractionHandler handler = new ContentExtractionHandler();

        Parser.PARSER.parse("<rewe-digital-content>test</rewe-digital-content>", handler);

        assertThat(handler.content()).isEqualTo("test");
    }

}
