package com.rewedigital.examples.msintegration.composer.proxy;

import java.util.Collections;
import java.util.concurrent.CompletionStage;

import com.damnhandy.uri.template.UriTemplate;
import com.rewedigital.examples.msintegration.composer.routing.BackendRouting.RouteMatch;
import com.spotify.apollo.Request;
import com.spotify.apollo.RequestContext;
import com.spotify.apollo.Response;

import okio.ByteString;

public class TemplateClient {

    public CompletionStage<Response<ByteString>> getTemplate(final RouteMatch match, final Request request,
        final RequestContext context) {
        final String expandedBackedUri = UriTemplate.fromTemplate(match.backend())
            .expand(Collections.<String, Object>unmodifiableMap(match.parsedPathArguments()));

        return context.requestScopedClient().send(Request.forUri(expandedBackedUri, request.method()));
    }

}
