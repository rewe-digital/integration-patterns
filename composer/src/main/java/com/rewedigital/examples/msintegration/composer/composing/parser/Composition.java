package com.rewedigital.examples.msintegration.composer.composing.parser;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class Composition {

    public interface Part {
        String enrichBody(String body);
    }

    private final List<Part> parts;

    public Composition() {
        this(new LinkedList<>());
    }

    public Composition(final List<Part> parts) {
       this.parts = parts;
    }

    public String body() {
        return parts.stream().reduce("", (r, p) -> p.enrichBody(r), (r, s) -> null);
    }

    @SuppressWarnings("unchecked")
    public <T extends Part> Optional<T> find(Class<T> type) {
        return parts.stream()
            .filter(p -> type.isAssignableFrom(p.getClass()))
            .map(p -> (T) p)
            .findFirst();
    }
}
