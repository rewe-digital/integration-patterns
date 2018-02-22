package com.rewedigital.examples.msintegration.composer.routing;

import java.util.concurrent.CompletionStage;

import com.rewedigital.examples.msintegration.composer.session.ResponseWithSession;
import com.rewedigital.examples.msintegration.composer.session.Session;
import com.spotify.apollo.Request;
import com.spotify.apollo.RequestContext;

import okio.ByteString;

public class TemplateClient {

    public CompletionStage<ResponseWithSession<ByteString>> fetch(final String path,
        final RequestContext context,
        final Session session) {
        return context.requestScopedClient()
            .send(session.enrich(Request.forUri(path, context.request().method())))
            .thenApply(r -> new ResponseWithSession<>(r, session.withValuesMergedFrom(Session.of(r))));
    }
}
