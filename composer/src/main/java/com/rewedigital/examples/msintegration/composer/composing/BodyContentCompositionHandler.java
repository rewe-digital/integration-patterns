package com.rewedigital.examples.msintegration.composer.composing;

import java.io.StringWriter;

import com.rewedigital.examples.msintegration.composer.composing.OngoingComposition.Handler;
import com.rewedigital.examples.msintegration.composer.composing.parser.Composition;
import com.rewedigital.examples.msintegration.composer.composing.parser.IncludedService;

public class BodyContentCompositionHandler implements Handler {

    public static class BodyContentPart implements Composition.Part {

        private final String content;

        public BodyContentPart(String content) {
            this.content = content;
        }

        public String content() {
            return content;
        }

        @Override
        public String enrichBody(String body) {
            return content;
        }
    }

    private final StringWriter writer;
    private final String template;
    private int currentIndex;
    private String result;

    public BodyContentCompositionHandler(final String template) {
        this.template = template;
        this.writer = new StringWriter(template.length());
        this.currentIndex = 0;
    }

    @Override
    public void handle(IncludedService.WithComposition includedContent) {
        includedContent
            .composition()
            .find(BodyContentPart.class)
            .ifPresent(p -> {
                writer.write(template, currentIndex, includedContent.startOffset() - currentIndex);
                writer.write(p.content());
                currentIndex = includedContent.endOffset();
            });
    }

    @Override
    public void finish() {
        if (result != null) {
            throw new IllegalStateException("Must not call finish() more than once");
        }
        writer.write(template, currentIndex, template.length() - currentIndex);
        this.result = writer.toString();
    }

    @Override
    public Composition.Part result() {
        if (result == null) {
            finish();
        }
        return new BodyContentPart(result);
    }

}
