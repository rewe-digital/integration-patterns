package com.rewedigital.examples.msintegration.composer.composingold.parser;

import java.util.concurrent.CompletableFuture;

import com.spotify.apollo.Response;

public interface Composer {
    CompletableFuture<Composition> compose(Response<String> template);
}