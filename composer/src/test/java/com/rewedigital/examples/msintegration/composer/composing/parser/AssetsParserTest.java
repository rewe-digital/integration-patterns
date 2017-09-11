package com.rewedigital.examples.msintegration.composer.composing.parser;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class AssetsParserTest {

    @Test
    public void parsesStylesheetLinksFromHead() {
        List<String> result = Parser.PARSER.parseAssets("<!DOCTYPE html><html>\n" +
            "<head>"
            + "<link href=\"../static/css/core.css\" data-rd-options=\"include-in-head\" rel=\"stylesheet\" media=\"screen\" />"
            + "<link href=\"../static/css/other.css\" data-rd-options=\"include-in-head\" rel=\"stylesheet\" media=\"screen\"></link>"
            + "<link href=\"../static/css/removed_from_head.css\" rel=\"stylesheet\" media=\"screen\" />"
            + "</head>\n" +
            "<body>"
            + "<link href=\"../static/css/removed_from_body.css\" rel=\"stylesheet\" media=\"screen\" />"
            + "</body></html>");

        assertEquals(
            Arrays.asList(
                "<link rel=\"stylesheet\" data-rd-options=\"include-in-head\" href=\"../static/css/core.css\" media=\"screen\" />",
                "<link rel=\"stylesheet\" data-rd-options=\"include-in-head\" href=\"../static/css/other.css\" media=\"screen\" />"),
            result);
    }

}
