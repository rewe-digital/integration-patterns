package com.rewedigital.examples.msintegration.composer.routing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.concurrent.CompletableFuture;

import org.junit.Test;

import com.rewedigital.examples.msintegration.composer.session.ResponseWithSession;
import com.rewedigital.examples.msintegration.composer.session.Sessions;
import com.spotify.apollo.Client;
import com.spotify.apollo.Request;
import com.spotify.apollo.RequestContext;
import com.spotify.apollo.Response;

import okio.ByteString;

public class SessionAwareProxyClientTest {

    @Test
    public void fetchesTemplateHandlingSession() throws Exception {
        final String path = "https://some.path/test";
        final String method = "GET";

        final Request expectedRequest = Request.forUri(path, method).withHeader("x-rd-key", "value");
        final Response<ByteString> response =
            Response.ok().withPayload(ByteString.EMPTY).withHeader("x-rd-response-key", "other-value");

        final RequestContext context = contextWith(aClient(expectedRequest, response), method);
        final ResponseWithSession<ByteString> templateResponse =
            new SessionAwareProxyClient().fetch(path, context, Sessions.sessionRoot("x-rd-key", "value")).toCompletableFuture()
                .get();

        assertThat(templateResponse.session().get("key")).contains("value");
        assertThat(templateResponse.session().get("response-key")).contains("other-value");
    }

    private RequestContext contextWith(final Client client, final String method) {
        final RequestContext context = mock(RequestContext.class);
        when(context.requestScopedClient()).thenReturn(client);
        final Request request = mock(Request.class);
        when(request.method()).thenReturn(method);
        when(context.request()).thenReturn(request);
        return context;
    }

    private Client aClient(final Request request, final Response<ByteString> response) {
        final Client client = mock(Client.class);
        when(client.send(request)).thenReturn(CompletableFuture.completedFuture(response));
        return client;
    }


    /*
     * return context.requestScopedClient() .send(session.enrich(Request.forUri(path, context.request().method())))
     * .thenApply(r -> new ResponseWithSession<>(r, session.mergedWith(SessionFragment.of(r))));
     * 
     */


}
