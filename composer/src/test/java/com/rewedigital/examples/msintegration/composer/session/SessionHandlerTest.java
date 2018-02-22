package com.rewedigital.examples.msintegration.composer.session;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.rewedigital.examples.msintegration.composer.session.SessionHandler.Interceptor;
import com.spotify.apollo.Request;
import com.spotify.apollo.Response;

public class SessionHandlerTest {

    @Test
    public void shouldAllowInterceptorToAddSessionAttribute() {
        final Interceptor interceptor = interceptorAdding("x-rd-some-key", "some-value");
        final SessionHandler lifecycle = new SimpleSessionLifecycle(asList(interceptor), Session.empty());

        final Session session = lifecycle.initialize(mock(Request.class));
        assertThat(session.get("some-key")).contains("some-value");
    }

    @Test
    public void shouldAllowInterceptorToOverwriteSessionId() {
        final Interceptor interceptor = interceptorAdding("x-rd-session-id", "some-value");
        final SessionHandler lifecycle =
            new SimpleSessionLifecycle(asList(new LocalSessionIdInterceptor(null), interceptor), Session.empty());

        final Session session = lifecycle.initialize(mock(Request.class));
        assertThat(session.getId()).contains("some-value");
    }

    private static Interceptor interceptorAdding(final String key, final String value) {
        return new Interceptor() {

            @Override
            public Session afterCreation(final Session session) {
                final Map<String, String> data = session.asHeaders();
                data.put(key, value);
                return Session.of(data);
            }
        };
    }

    private static class SimpleSessionLifecycle extends SessionHandler {

        private Session initial;

        protected SimpleSessionLifecycle(final List<Interceptor> interceptors, final Session initial) {
            super(interceptors);
            this.initial = initial;
        }

        @Override
        public <T> Response<T> writeTo(final Response<T> response, final Map<String, String> sessionData,
            final boolean dirty) {
            return response;
        }

        @Override
        protected Session obtainSession(final Request request) {
            return initial;
        }

    }

}
