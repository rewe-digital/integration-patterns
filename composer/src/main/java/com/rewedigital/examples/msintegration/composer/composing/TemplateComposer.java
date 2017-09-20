package com.rewedigital.examples.msintegration.composer.composing;

import java.util.concurrent.CompletableFuture;

import com.spotify.apollo.Response;


public interface TemplateComposer {

    CompletableFuture<Response<String>> composeTemplate(final Response<String> templateResponse);

}
