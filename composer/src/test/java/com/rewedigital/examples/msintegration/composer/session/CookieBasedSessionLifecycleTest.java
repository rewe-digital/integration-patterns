package com.rewedigital.examples.msintegration.composer.session;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.spotify.apollo.Request;
import com.spotify.apollo.RequestContext;
import com.spotify.apollo.Response;

public class CookieBasedSessionLifecycleTest {

    @Test
    public void shouldWriteAndReadCookie() {
        final CookieBasedSessionLifecycle lifecycle = new CookieBasedSessionLifecycle(configuration());
        final String cookieHeader =
            lifecycle.writeTo(Response.ok(), session("x-rd-key", "value")).header("Set-Cookie").get();
        final Session session =
            lifecycle.buildSession(contextOf((Request.forUri("/").withHeader("Cookie", cookieHeader))));
        assertThat(session.get("key")).contains("value");
    }

    private SessionConfiguration configuration() {
        return new SessionConfiguration(true, "sessioncookie", "HS512");
    }

    private static Map<String, String> session(final String key, final String value) {
        final HashMap<String, String> result = new HashMap<>();
        result.put(key, value);
        return result;
    }

    private static RequestContext contextOf(final Request request) {
        final RequestContext result = mock(RequestContext.class);
        when(result.request()).thenReturn(request);
        return result;
    }

}
