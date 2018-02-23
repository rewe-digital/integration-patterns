package com.rewedigital.examples.msintegration.composer.session;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.HashMap;
import java.util.Optional;

import org.junit.Test;

import com.rewedigital.examples.msintegration.composer.configuration.DefaultConfiguration;
import com.spotify.apollo.Request;
import com.spotify.apollo.RequestContext;
import com.spotify.apollo.Response;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigValueFactory;

public class CookieBasedSessionHandlerTest {

    @Test
    public void shouldWriteAndReadCookie() {
        final String cookieHeader =
            dirtySession("x-rd-key", "value").writeTo(Response.ok(), sessionHandler()).header("Set-Cookie").get();
        final SessionRoot session =
            sessionHandler().initialize(contextFor(Request.forUri("/").withHeader("Cookie", cookieHeader)));
        assertThat(session.get("key")).contains("value");
    }

    @Test
    public void shouldCreateNewSessionIfNonePresent() {
        final SessionRoot session = sessionHandler().initialize(contextFor(Request.forUri("/")));
        assertThat(session).isNotNull();
    }

    @Test
    public void shouldNotWriteSessionCookieIfSessionIsNotDirty() {
        final Optional<String> setCookieHeader =
            cleanSession("x-rd-key", "value").writeTo(Response.ok(), sessionHandler()).header("Set-Cookie");
        assertThat(setCookieHeader).isEmpty();
    }

    @Test
    public void shouldBeConstructedWithFactory() {
        final SessionHandler result = new CookieBasedSessionHandler.Factory(enabledConfig()).build();
        assertThat(result).isInstanceOf(CookieBasedSessionHandler.class);
    }

    @Test
    public void shouldConstructNoopInstanceIfSessionHandlingIsDisabled() {
        final SessionHandler result = new CookieBasedSessionHandler.Factory(disabledConfig()).build();
        assertThat(result).isEqualTo(SessionHandler.noSession());
    }

    private static SessionRoot cleanSession(final String key, final String value) {
        return session(key, value, false);
    }

    private static SessionRoot dirtySession(final String key, final String value) {
        return session(key, value, true);
    }
    
    private static SessionRoot session(final String key, final String value, final boolean dirty) {
        final HashMap<String, String> data = new HashMap<>();
        data.put(key, value);
        return SessionRoot.of(data, dirty);
    }

    private CookieBasedSessionHandler sessionHandler() {
        return new CookieBasedSessionHandler(configuration());
    }

    private RequestContext contextFor(final Request request) {
        final RequestContext context = mock(RequestContext.class);
        when(context.request()).thenReturn(request);
        return context;
    }

    private SessionConfiguration configuration() {
        return new SessionConfiguration(true, "sessioncookie", "HS512", Collections.emptyList());
    }

    private Config enabledConfig() {
        return DefaultConfiguration.defaultConfiguration().getConfig("composer.session");
    }

    private Config disabledConfig() {
        return enabledConfig().withValue("enabled", ConfigValueFactory.fromAnyRef(Boolean.FALSE));
    }
}
