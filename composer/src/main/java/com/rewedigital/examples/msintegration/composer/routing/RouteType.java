package com.rewedigital.examples.msintegration.composer.routing;

import java.util.concurrent.CompletionStage;

import com.rewedigital.examples.msintegration.composer.routing.BackendRouting.RouteMatch;
import com.rewedigital.examples.msintegration.composer.session.ResponseWithSession;
import com.rewedigital.examples.msintegration.composer.session.Session;
import com.spotify.apollo.RequestContext;

import okio.ByteString;

public interface RouteType {

    CompletionStage<ResponseWithSession<ByteString>> execute(final RouteMatch rm, final RequestContext context,
        final Session session);
}
