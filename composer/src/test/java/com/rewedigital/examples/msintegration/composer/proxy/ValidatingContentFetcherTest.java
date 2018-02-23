package com.rewedigital.examples.msintegration.composer.proxy;

import static java.util.Collections.emptyMap;
import static java.util.concurrent.CompletableFuture.completedFuture;
import static okio.ByteString.encodeUtf8;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

import org.junit.Test;
import org.mockito.ArgumentMatcher;

import com.rewedigital.examples.msintegration.composer.composing.ValidatingContentFetcher;
import com.rewedigital.examples.msintegration.composer.session.SessionRoot;
import com.spotify.apollo.Client;
import com.spotify.apollo.Request;
import com.spotify.apollo.Response;

import okio.ByteString;

public class ValidatingContentFetcherTest {
    
    private final SessionRoot emptySession = SessionRoot.empty();

    @Test
    public void fetchesEmptyContentForMissingPath() throws Exception {
        final Response<String> result =
            new ValidatingContentFetcher(mock(Client.class), emptyMap(), emptySession).fetch(null, null).get();
        assertThat(result.payload()).contains("");
    }

    @Test
    public void fetchesContentFromPath() throws Exception {
        final Client client = mock(Client.class);
        when(client.send(aRequestWithPath("/some/path"))).thenReturn(aResponse("ok"));
        final Response<String> result =
            new ValidatingContentFetcher(client, emptyMap(), emptySession).fetch("/some/path", "fallback").get();
        assertThat(result.payload()).contains("ok");
    }

    @Test
    public void expandsPathWithParameters() throws Exception {
        final Client client = mock(Client.class);
        when(client.send(aRequestWithPath("/some/path/val"))).thenReturn(aResponse("ok"));
        final Response<String> result =
            new ValidatingContentFetcher(client, params("var", "val"), emptySession)
                .fetch("/some/path/{var}", "fallback").get();
        assertThat(result.payload()).contains("ok");
    }

    @Test
    public void defaultsNonHTMLResponsesToEmpty() throws Exception {
        final Client client = mock(Client.class);
        when(client.send(aRequestWithPath("/some/path"))).thenReturn(aResponse("ok", "text/json"));
        final Response<String> result =
            new ValidatingContentFetcher(client, emptyMap(), emptySession).fetch("/some/path", "").get();
        assertThat(result.payload()).contains("");
    }

    @Test
    public void forwardsSession() throws Exception {
        final Client client = mock(Client.class);
        when(client.send(any())).thenReturn(aResponse(""));
        final SessionRoot session = session("x-rd-key", "value");
        new ValidatingContentFetcher(client, emptyMap(), session).fetch("/some/path", "").get();
        verify(client).send(aRequestWithSession("x-rd-key", "value"));
    }

    private static Request aRequestWithPath(final String path) {
        return argThat(new ArgumentMatcher<Request>() {

            @Override
            public boolean matches(final Object argument) {
                return (Request.class.isAssignableFrom(argument.getClass())) &&
                    path.equals(((Request) argument).uri());
            }
        });
    }

    private static Request aRequestWithSession(final String key, final String value) {
        return argThat(new ArgumentMatcher<Request>() {

            @Override
            public boolean matches(final Object argument) {
                return Request.class.isAssignableFrom(argument.getClass())
                    && ((Request) argument).header(key).equals(Optional.of(value));
            }
        });
    }

    private static CompletionStage<Response<ByteString>> aResponse(final String payload) {
        return aResponse(payload, "text/html");
    }

    private static CompletionStage<Response<ByteString>> aResponse(final String payload, final String contentType) {
        return completedFuture(
            Response.forPayload(encodeUtf8(payload)).withHeader("Content-Type", contentType));
    }

    private static SessionRoot session(final String key, final String value) {
        return SessionRoot.of(params(key, value));
    }

    private static <T> Map<String, T> params(final String name, final T value) {
        final Map<String, T> params = new HashMap<>();
        params.put(name, value);
        return params;
    }
}
