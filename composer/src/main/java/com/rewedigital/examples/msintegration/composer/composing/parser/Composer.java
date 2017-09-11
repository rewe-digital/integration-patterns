package com.rewedigital.examples.msintegration.composer.composing.parser;

import java.util.concurrent.CompletionStage;

import com.spotify.apollo.Response;

public interface Composer {
    CompletionStage<Composition> compose(Response<String> template);
}