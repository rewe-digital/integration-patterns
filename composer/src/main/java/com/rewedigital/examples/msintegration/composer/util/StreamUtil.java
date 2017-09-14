package com.rewedigital.examples.msintegration.composer.util;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StreamUtil {

    public static <T> CompletableFuture<Stream<T>> flatten(final Stream<CompletableFuture<T>> com) {
        final List<CompletableFuture<T>> list = com.map(c -> c.toCompletableFuture()).collect(Collectors.toList());
        return CompletableFuture.allOf(list.toArray(new CompletableFuture[list.size()]))
            .thenApply(v -> list.stream().map(CompletableFuture::join));
    }

}
