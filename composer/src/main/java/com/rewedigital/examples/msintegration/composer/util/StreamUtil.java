package com.rewedigital.examples.msintegration.composer.util;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StreamUtil {

	public static <T> CompletionStage<Stream<T>> flatten(final Stream<CompletionStage<T>> com) {
		final List<CompletableFuture<T>> list = com.map(c -> c.toCompletableFuture()).collect(Collectors.toList());
		return CompletableFuture.allOf(list.toArray(new CompletableFuture[list.size()]))
				.thenApply(v -> list.stream().map(CompletableFuture::join));
	}

}
