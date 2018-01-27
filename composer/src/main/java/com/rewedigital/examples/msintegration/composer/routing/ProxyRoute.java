package com.rewedigital.examples.msintegration.composer.routing;

import java.util.Objects;
import java.util.concurrent.CompletionStage;

import com.rewedigital.examples.msintegration.composer.proxy.TemplateClient;
import com.rewedigital.examples.msintegration.composer.routing.BackendRouting.RouteMatch;
import com.rewedigital.examples.msintegration.composer.session.ResponseWithSession;
import com.rewedigital.examples.msintegration.composer.session.Session;
import com.spotify.apollo.RequestContext;

import okio.ByteString;

public class ProxyRoute implements RouteType {

    private final TemplateClient templateClient;

    public ProxyRoute(final TemplateClient templateClient) {
        this.templateClient = Objects.requireNonNull(templateClient);
    }

    @Override
    public CompletionStage<ResponseWithSession<ByteString>> execute(final RouteMatch rm, final RequestContext context,
        final Session session) {
        return templateClient.fetch(rm.expandedPath(), context, session);
    }
}
