package com.rewedigital.examples.msintegration.composer.util;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StreamUtil {

	public static <T> CompletableFuture<Stream<T>> flatten(Stream<CompletableFuture<T>> com) {
		List<CompletableFuture<T>> list = com.collect(Collectors.toList());
		return CompletableFuture.allOf(list.toArray(new CompletableFuture[list.size()]))
				.thenApply(v -> list.stream().map(CompletableFuture::join));
	}
}
