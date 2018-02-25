package com.rewedigital.examples.msintegration.composer.routing;

import java.util.concurrent.CompletionStage;

import com.rewedigital.examples.msintegration.composer.session.ResponseWithSession;
import com.rewedigital.examples.msintegration.composer.session.SessionRoot;
import com.spotify.apollo.RequestContext;

import okio.ByteString;

public interface RouteType {

    CompletionStage<ResponseWithSession<ByteString>> execute(final RouteMatch rm, final RequestContext context,
        final SessionRoot session);
}
