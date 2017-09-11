package com.rewedigital.examples.msintegration.composer.composing;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import com.rewedigital.examples.msintegration.composer.composing.parser.Composition;
import com.rewedigital.examples.msintegration.composer.composing.parser.Composition.Part;
import com.rewedigital.examples.msintegration.composer.composing.parser.IncludedService.WithComposition;

public class AssetLinkCompositionHander implements OngoingComposition.Handler {

    private static class AssetLinkPart implements Composition.Part {
        private final List<String> assetLinks;

        public AssetLinkPart(final List<String> assetLinks) {
            this.assetLinks = assetLinks;
        }

        @Override
        public String enrichBody(String body) {
            final String assets = assetLinks.stream()
                .distinct()
                .collect(Collectors.joining("\n"));
            return body.replaceFirst("</head>", assets + "\n</head>");
        }
    }

    public static final String STYLESHEET_HEADER = "x-uic-stylesheet";

    private final List<String> assetLinks = new LinkedList<>();

    @Override
    public void handle(final WithComposition includedContent) {
        includedContent.composition()
            .find(AssetLinkPart.class)
            .ifPresent(l -> assetLinks.addAll(l.assetLinks));
        includedContent.response().header(STYLESHEET_HEADER)
            .map(href -> buildCssLink(href))
            .ifPresent(link -> assetLinks.add(link));
    }

    private String buildCssLink(final String href) {
        return String.format("<link rel=\"stylesheet\" href=\"%s\" />", href);
    }

    @Override
    public void finish() {
        // NOOP
    }

    @Override
    public Part result() {
        return new AssetLinkPart(assetLinks);
    }

}
