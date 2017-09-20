package com.rewedigital.examples.msintegration.composer.composing;

import java.io.StringWriter;
import java.util.List;
import java.util.stream.Collectors;

import com.rewedigital.examples.msintegration.composer.session.Session;
import com.spotify.apollo.Response;

class Composition {

    private final List<Composition> children;
    private final List<String> assetLinks;
    private final int startOffset;
    private final int endOffset;
    private final String template;
    private final ContentRange contentRange;
    private final Session session;

    public Composition(final String template, final ContentRange contentRange, final List<String> assetLinks,
        final List<Composition> children) {
        this(0, template.length(), template, contentRange, assetLinks, Session.empty(), children);
    }

    private Composition(final int startOffset, final int endOffset, final String template,
        final ContentRange contentRange, final List<String> assetLinks, final Session session,
        final List<Composition> children) {
        this.startOffset = startOffset;
        this.endOffset = endOffset;
        this.template = template;
        this.contentRange = contentRange;
        this.assetLinks = assetLinks;
        this.children = children;
        this.session = session;
    }

    public Composition forRange(int startOffset, int endOffset) {
        return new Composition(startOffset, endOffset, template, contentRange, assetLinks, Session.empty(), children);
    }

    public Composition withSession(final Session session) {
        return new Composition(startOffset, endOffset, template, contentRange, assetLinks, session, children);
    }

    // TODO TV make the recursion more explicit
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
        return mergedSession().writeTo(Response.forPayload(withAssetLinks(body())));
    }

    private Session mergedSession() {
        return children.stream()
            .reduce(session, (s, c) -> s.mergeWith(c.mergedSession()), (a, b) -> a);
    }

    private String withAssetLinks(String body) {
        final String assets = assetLinks.stream()
            .distinct()
            .collect(Collectors.joining("\n"));
        return body.replaceFirst("</head>", assets + "\n</head>");
    }


}
