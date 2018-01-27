package com.rewedigital.examples.msintegration.composer.proxy;

import java.util.concurrent.CompletionStage;

import com.damnhandy.uri.template.UriTemplate;
import com.rewedigital.examples.msintegration.composer.routing.BackendRouting.RouteMatch;
import com.rewedigital.examples.msintegration.composer.session.ResponseWithSession;
import com.rewedigital.examples.msintegration.composer.session.Session;
import com.spotify.apollo.Request;
import com.spotify.apollo.RequestContext;

import okio.ByteString;

public class TemplateClient {

    public CompletionStage<ResponseWithSession<ByteString>> fetch(final RouteMatch match,
        final RequestContext context,
        final Session session) {
        return context.requestScopedClient()
            .send(session.enrich(Request.forUri(expandPath(match), context.request().method())))
            .thenApply(r -> new ResponseWithSession<>(r, session.mergeWith(Session.of(r))));
    }

    private String expandPath(final RouteMatch match) {
        return UriTemplate.fromTemplate(match.backend()).expand(match.parsedPathArguments());
    }

}
