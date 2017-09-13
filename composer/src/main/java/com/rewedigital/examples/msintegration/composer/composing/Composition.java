package com.rewedigital.examples.msintegration.composer.composing;

import java.io.StringWriter;
import java.util.List;
import java.util.stream.Collectors;

import com.spotify.apollo.Response;

class Composition {

    private final List<Composition> children;
    private final List<String> assetLinks;
    private final int startOffset;
    private final int endOffset;
    private final String template;
    private final ContentRange contentRange;

    public Composition(final String template, final ContentRange contentRange, final List<String> assetLinks,
        final List<Composition> children) {
        this(0, template.length(), template, contentRange, assetLinks, children);
    }

    private Composition(final int startOffset, final int endOffset, final String template,
        final ContentRange contentRange, final List<String> assetLinks,
        final List<Composition> children) {
        this.startOffset = startOffset;
        this.endOffset = endOffset;
        this.template = template;
        this.contentRange = contentRange;
        this.assetLinks = assetLinks;
        this.children = children;
    }

    public Composition forRange(int startOffset, int endOffset) {
        return new Composition(startOffset, endOffset, template, contentRange, assetLinks, children);
    }

    private String body() {
        final StringWriter writer = new StringWriter(template.length());
        int currentIndex = contentRange.start();
        for (final Composition c : children) {
            writer.write(template, currentIndex, c.startOffset - currentIndex);
            writer.write(c.body());
            currentIndex = c.endOffset;
            assetLinks.addAll(c.assetLinks);
        }
        writer.write(template, currentIndex, contentRange.end() - currentIndex);
        return writer.toString();
    }

    public Response<String> toResponse() {
        return Response.forPayload(withAssetLinks(body()));
    }

    private String withAssetLinks(String body) {
        final String assets = assetLinks.stream()
            .distinct()
            .collect(Collectors.joining("\n"));
        return body.replaceFirst("</head>", assets + "\n</head>");
    }

}
