package com.rewedigital.examples.msintegration.composer.composing;

import java.util.concurrent.CompletableFuture;

import com.rewedigital.examples.msintegration.composer.session.ResponseWithSession;
import com.spotify.apollo.Response;


public interface TemplateComposer {

    CompletableFuture<ResponseWithSession<String>> composeTemplate(final Response<String> templateResponse);

}
