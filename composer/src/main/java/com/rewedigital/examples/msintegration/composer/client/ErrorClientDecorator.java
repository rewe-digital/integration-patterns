package com.rewedigital.examples.msintegration.composer.client;

import com.spotify.apollo.Response;
import com.spotify.apollo.Status;
import com.spotify.apollo.environment.ClientDecorator;
import com.spotify.apollo.environment.IncomingRequestAwareClient;

import okio.ByteString;

public class ErrorClientDecorator implements ClientDecorator {

    @Override
    public IncomingRequestAwareClient apply(final IncomingRequestAwareClient requestAwareClient) {
        return (request, incoming) -> requestAwareClient.send(request, incoming)
            .exceptionally(ex -> {
                return Response.of(Status.INTERNAL_SERVER_ERROR, ByteString.encodeUtf8(""));
            });
    }

}
