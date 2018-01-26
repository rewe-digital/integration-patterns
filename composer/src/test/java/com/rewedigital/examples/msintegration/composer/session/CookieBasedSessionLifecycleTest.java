package com.rewedigital.examples.msintegration.composer.session;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Optional;

import org.junit.Test;

import com.spotify.apollo.Request;
import com.spotify.apollo.RequestContext;
import com.spotify.apollo.Response;

public class CookieBasedSessionLifecycleTest {

    private final CookieBasedSessionLifecycle sessionLifecycle = new CookieBasedSessionLifecycle(configuration());

    @Test
    public void shouldWriteAndReadCookie() {
        final String cookieHeader =
            dirtySession("x-rd-key", "value").writeTo(Response.ok(), sessionLifecycle).header("Set-Cookie").get();
        final Session session =
            sessionLifecycle.buildSession(contextOf((Request.forUri("/").withHeader("Cookie", cookieHeader))));
        assertThat(session.get("key")).contains("value");
    }

    @Test
    public void shouldCreateNewSessionIfNonePresent() {
        final Session session = sessionLifecycle.buildSession(contextOf((Request.forUri("/"))));
        assertThat(session).isNotNull();
        assertThat(session.isDirty()).isTrue();
    }

    @Test
    public void shouldNotWriteSessionCookieIfSessionIsNotDirty() {
        final Optional<String> setCookieHeader =
            cleanSession("x-rd-key", "value").writeTo(Response.ok(), sessionLifecycle).header("Set-Cookie");
        assertThat(setCookieHeader).isEmpty();
    }

    private SessionConfiguration configuration() {
        return new SessionConfiguration(true, "sessioncookie", "HS512");
    }

    private static Session cleanSession(final String key, final String value) {
        final HashMap<String, String> data = new HashMap<>();
        data.put(key, value);
        return Session.of(data);
    }

    private static Session dirtySession(final String key, final String value) {
        return Session.empty().mergeWith(cleanSession(key, value));
    }

    private static RequestContext contextOf(final Request request) {
        final RequestContext result = mock(RequestContext.class);
        when(result.request()).thenReturn(request);
        return result;
    }

}
