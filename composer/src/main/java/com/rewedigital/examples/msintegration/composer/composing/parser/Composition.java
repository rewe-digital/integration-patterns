package com.rewedigital.examples.msintegration.composer.composing.parser;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class Composition {

    public interface Part {
        String enrichBody(String body);
    }

    private final List<String> assetLinks;

    private final List<Part> parts;

    public Composition() {
        this("", Collections.synchronizedList(new LinkedList<>()), new LinkedList<>());
    }

    public Composition(final List<Part> parts) {
        this("", new LinkedList<>(), parts);
    }

    public Composition(final String body, final List<String> assetLinks) {
        this(body, assetLinks, new LinkedList<>());
    }

    public Composition(final String body, final List<String> assetLinks, List<Part> parts) {
        this.assetLinks = assetLinks;
        this.parts = parts;
    }

    public String body() {
        String result = "";
        for (Part p : parts) {
            result = p.enrichBody(result);
        }
        return result;
    }

    public void addAssetLink(final String link) {
        assetLinks.add(link);
    }

    @SuppressWarnings("unchecked")
    public <T extends Part> Optional<T> find(Class<T> type) {
        return parts.stream()
            .filter(p -> type.isAssignableFrom(p.getClass()))
            .map(p -> (T) p)
            .findFirst();
    }
}
