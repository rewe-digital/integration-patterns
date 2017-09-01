package com.rewedigital.examples.msintegration.composer.composing;

import java.util.concurrent.CompletionStage;

import com.spotify.apollo.Client;
import com.spotify.apollo.Request;
import com.spotify.apollo.RequestContext;
import com.spotify.apollo.Response;

import okio.ByteString;

public class ComposerFactory {

    public Composer forRequestContext(final RequestContext requestContext) {
        final Client client = requestContext.requestScopedClient();
        Client wrapper = new Client() {
            @Override
            public CompletionStage<Response<ByteString>> send(Request request) {
                return client.send(request)
                    .exceptionally(ex -> {
                        return Response.forPayload(ByteString.encodeUtf8(""));
                    });
            }
        };
        return new Composer(wrapper);
    }
}
