package com.rewedigital.examples.msintegration.composer.composing.parser;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class Content {

    private final String body;
    private final List<String> assetLinks;

    public Content() {
        this("", Collections.synchronizedList(new LinkedList<>()));
    }

    public Content(final String body, final List<String> assetLinks) {
        this.body = body;
        this.assetLinks = assetLinks;
    }

    public String body() {
        return body;
    }

    public List<String> assetLinks() {
        return assetLinks;
    }

    public void addAssetLink(final String link) {
        assetLinks.add(link);
    }

    public String contentWithAssetLinks() {
        final String assets = assetLinks.stream()
            .distinct()
            .collect(Collectors.joining("\n"));
        return body.replaceFirst("</head>", assets + "\n</head>");
    }
}
