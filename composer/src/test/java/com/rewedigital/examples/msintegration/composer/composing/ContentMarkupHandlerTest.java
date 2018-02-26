package com.rewedigital.examples.msintegration.composer.composing;

import static com.rewedigital.examples.msintegration.composer.parser.Parser.PARSER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class ContentMarkupHandlerTest {

    private static final ContentRange defaultContentRange = new ContentRange(123, 456);

    @Test
    public void parsesStylesheetLinksFromHead() {
        List<String> result = parse("<!DOCTYPE html><html>\n" +
            "<head>"
            + "<link href=\"../static/css/core.css\" data-rd-options=\"include-in-head\" rel=\"stylesheet\" media=\"screen\" />"
            + "<link href=\"../static/css/other.css\" data-rd-options=\"include-in-head\" rel=\"stylesheet\" media=\"screen\"></link>"
            + "<link href=\"../static/css/removed_from_head.css\" rel=\"stylesheet\" media=\"screen\" />"
            + "</head>\n" +
            "<body>"
            + "<link href=\"../static/css/removed_from_body.css\" rel=\"stylesheet\" media=\"screen\" />"
            + "</body></html>").assetLinks();

        assertEquals(
            Arrays.asList(
                "<link rel=\"stylesheet\" data-rd-options=\"include-in-head\" href=\"../static/css/core.css\" media=\"screen\" />",
                "<link rel=\"stylesheet\" data-rd-options=\"include-in-head\" href=\"../static/css/other.css\" media=\"screen\" />"),
            result);
    }

    @Test
    public void parsesContentRangeFromMarkup() {
        final ContentRange content = parse("aa<rewe-digital-content>test</rewe-digital-content>bb").contentRange();
        assertThat(content).isEqualTo(new ContentRange(24, 28));
    }

    @Test
    public void usesDefaultContentRangeIfMissingContentTags() {
        final ContentRange content = parse("aa test bb").contentRange();
        assertThat(content).isEqualTo(defaultContentRange);
    }

    private ContentMarkupHandler parse(final String data) {
        final ContentMarkupHandler markupHandler =
            new ContentMarkupHandler(defaultContentRange,
                new ComposerHtmlConfiguration("", "rewe-digital-content", "data-rd-options"));
        PARSER.parse(data, markupHandler);
        return markupHandler;
    }

}
