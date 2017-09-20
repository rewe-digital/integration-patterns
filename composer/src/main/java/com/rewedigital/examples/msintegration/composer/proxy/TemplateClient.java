package com.rewedigital.examples.msintegration.composer.proxy;

import java.util.concurrent.CompletionStage;

import com.damnhandy.uri.template.UriTemplate;
import com.rewedigital.examples.msintegration.composer.routing.BackendRouting.RouteMatch;
import com.rewedigital.examples.msintegration.composer.session.Session;
import com.spotify.apollo.Request;
import com.spotify.apollo.RequestContext;
import com.spotify.apollo.Response;

import okio.ByteString;

public class TemplateClient {

    public CompletionStage<Response<ByteString>> getTemplate(final RouteMatch match, final RequestContext context,
        final Session session) {
        return context.requestScopedClient().send(Request.forUri(expandPath(match), context.request().method()));
    }

    private String expandPath(final RouteMatch match) {
        return UriTemplate.fromTemplate(match.backend()).expand(match.parsedPathArguments());
    }

}
