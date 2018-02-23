package com.rewedigital.examples.msintegration.composer.session;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.spotify.apollo.RequestContext;
import com.spotify.apollo.RequestMetadata;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigValueFactory;

public class LocalSessionIdInterceptorTest {

    private static final int ttl = 2000;
    private static final int renewAfter = 500;
    private static final int expireAt = 1000;
    private static final int beforeRenewalRequired = 100;
    private static final int beforeExpiration = 900;
    private static final int afterExpiration = 4000;
    private static final int now = 0;

    @Test
    public void createsSessionIdForNewSession() {
        final LocalSessionIdInterceptor interceptor = new LocalSessionIdInterceptor(config());
        final Session session = interceptor.afterCreation(Session.empty(), context());
        assertThat(session.getId()).isPresent();
    }

    @Test
    public void retainsExistingSessionId() {
        final Session sessionWithId = Session.empty().withId("the session id");
        final LocalSessionIdInterceptor interceptor = new LocalSessionIdInterceptor(config());
        final Session session = interceptor.afterCreation(sessionWithId, context());
        assertThat(session.getId()).contains("the session id");
    }

    @Test
    public void addsExpirationWithTTLFromConfig() {
        final LocalSessionIdInterceptor interceptor = new LocalSessionIdInterceptor(config(ttl));
        final Session session = interceptor.afterCreation(Session.empty(), contextForArrivalTime(now));
        assertThat(session.rawData().get("expires-at")).isEqualTo(Long.toString(now + ttl));
    }

    @Test
    public void createsNewSessionIfExpired() {
        final Session expiredSession = sessionExpiringAt(expireAt, "x-rd-key", "value");
        final LocalSessionIdInterceptor interceptor = new LocalSessionIdInterceptor(config(ttl));
        final Session session = interceptor.afterCreation(expiredSession, contextForArrivalTime(afterExpiration));
        assertThat(session.get("key")).isNotPresent();
        assertThat(session.isDirty());
    }

    @Test
    public void updatesExpiration() {
        final Session initialSession = sessionExpiringAt(expireAt, "x-rd-key", "value");
        final LocalSessionIdInterceptor interceptor = new LocalSessionIdInterceptor(config(ttl));
        final Session session = interceptor.afterCreation(initialSession, contextForArrivalTime(beforeExpiration));
        assertThat(session.get("key")).contains("value");
        assertThat(session.rawData().get("expires-at")).isEqualTo(Long.toString(beforeExpiration + ttl));
        assertThat(session.isDirty()).isTrue();
    }

    @Test
    public void doesNotUpdateExpirationTimeBeforeGracePeriod() {
        final Session initialSession = sessionExpiringAt(expireAt, "x-rd-key", "value");
        final LocalSessionIdInterceptor interceptor = new LocalSessionIdInterceptor(config(ttl, renewAfter));
        final Session session =
            interceptor.afterCreation(initialSession, contextForArrivalTime(beforeRenewalRequired));
        assertThat(session.get("key")).contains("value");
        assertThat(session.rawData().get("expires-at")).isEqualTo(Long.toString(expireAt));
        assertThat(session.isDirty()).isFalse();
    }

    private Session sessionExpiringAt(final long epochSeconds, final String key, final String value) {
        final Map<String, String> data = new HashMap<>();
        data.put("x-rd-session-id", "1234");
        data.put("expires-at", Long.toString(epochSeconds));
        data.put(key, value);
        return Session.of(data);
    }

    private RequestContext context() {
        return contextForArrivalTime(0);
    }

    private RequestContext contextForArrivalTime(final int now) {
        final RequestContext context = mock(RequestContext.class);
        final RequestMetadata requestMetadata = mock(RequestMetadata.class);
        when(context.metadata()).thenReturn(requestMetadata);
        when(requestMetadata.arrivalTime()).thenReturn(Instant.ofEpochSecond(now));
        return context;
    }

    private Config config() {
        return config(10000L);
    }

    private Config config(final long ttl) {
        return config(ttl, ttl);
    }


    private Config config(final long ttl, final long renewafter) {
        return ConfigFactory.empty().withValue("ttl", ConfigValueFactory.fromAnyRef(ttl)).withValue("renew-after",
            ConfigValueFactory.fromAnyRef(renewafter));
    }

}
