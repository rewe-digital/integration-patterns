package com.rewedigital.examples.msintegration.composer.composing;

import java.util.concurrent.CompletableFuture;

import com.spotify.apollo.Response;

public interface ContentComposer {
    CompletableFuture<Composition> composeContent(final Response<String> templateResponse);
}