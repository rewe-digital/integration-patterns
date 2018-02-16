package com.rewedigital.examples.msintegration.composer.session;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.rewedigital.examples.msintegration.composer.session.SessionLifecycle.Interceptor;
import com.spotify.apollo.Request;
import com.spotify.apollo.Response;

public class SessionLifecycleTest {

    @Test
    public void shouldRunSessionInterceptorAfterObtainingInitialSession() {
        final Interceptor interceptor = mock(Interceptor.class);
        final SessionLifecycle lifecycle = new SimpleSessionLifecycle(asList(interceptor), Session.empty());

        lifecycle.obtainSession(mock(Request.class));
        verify(interceptor).afterCreation(any(Session.class));
    }

    @Test
    public void shouldAllowInterceptorToAddSessionAttribute() {
        final Interceptor interceptor = interceptorAdding("x-rd-some-key", "some-value");
        final SessionLifecycle lifecycle = new SimpleSessionLifecycle(asList(interceptor), Session.empty());

        final Session session = lifecycle.obtainSession(mock(Request.class));
        assertThat(session.get("some-key")).contains("some-value");

    }

    private static Interceptor interceptorAdding(final String key, final String value) {
        return new Interceptor() {

            @Override
            public Session afterCreation(final Session session) {
                final Map<String, String> data = new HashMap<>();
                data.put(key, value);
                return session.mergeWith(Session.of(data));
            }
        };
    }


    private static class SimpleSessionLifecycle extends SessionLifecycle {

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
        protected Session createSession(final Request request) {
            return initial;
        }

    }

}
