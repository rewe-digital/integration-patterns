package com.rewedigital.examples.msintegration.composer.session;

import static com.rewedigital.examples.msintegration.composer.session.Sessions.sessionRoot;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.Test;
import org.mockito.Mockito;

import com.spotify.apollo.Client;
import com.spotify.apollo.Request;
import com.spotify.apollo.RequestContext;
import com.spotify.apollo.Response;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigValueFactory;

import okio.ByteString;

public class RemoteHttpSessionInterceptorTest {

    @Test
    public void shouldCallConfiguredUrlToUpdateSession() throws Exception {
        final String url = "https://test.uri/session";
        final RemoteHttpSessionInterceptor interceptor =
            new RemoteHttpSessionInterceptor(config().withValue("url", ConfigValueFactory.fromAnyRef(url)));

        final Client client = mock(Client.class);
        when(client.send(requestFor("POST", url, "{\"x-rd-key\":\"value\"}")))
            .thenReturn(response("{\"x-rd-key\":\"new-value\"}"));

        final SessionRoot session =
            interceptor.afterCreation(sessionRoot("x-rd-key", "value"), contextWith(client))
                .toCompletableFuture().get();
        assertThat(session.get("key")).contains("new-value");
    }

    private Request requestFor(final String method, final String url, final String payload) {
        return Mockito.argThat(new BaseMatcher<Request>() {

            @Override
            public boolean matches(final Object item) {
                if (!(item instanceof Request)) {
                    return false;
                }

                final Request request = (Request) item;
                return url.equals(request.uri()) && method.equals(request.method())
                    && request.payload().isPresent() && payload.equals(request.payload().get().utf8());
            }

            @Override
            public void describeTo(final Description description) {
                description.appendText("request").appendText(" method=").appendValue(method).appendText(" url=")
                    .appendValue(url).appendText(" payload=").appendValue(payload);
            }
        });
    }

    private CompletionStage<Response<ByteString>> response(final String payload) {
        return CompletableFuture.completedFuture(Response.forPayload(ByteString.encodeUtf8(payload)));
    }

    private Config config() {
        return ConfigFactory.empty();
    }

    private static RequestContext contextWith(final Client client) {
        final RequestContext context = mock(RequestContext.class);
        when(context.requestScopedClient()).thenReturn(client);
        return context;
    }

}
