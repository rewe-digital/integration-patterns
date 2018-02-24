package com.rewedigital.examples.msintegration.composer.session;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import org.junit.Test;

import com.rewedigital.examples.msintegration.composer.session.SessionHandler.Interceptor;
import com.spotify.apollo.Request;
import com.spotify.apollo.RequestContext;
import com.spotify.apollo.Response;

public class SessionHandlerTest {

    @Test
    public void shouldAllowInterceptorToAddSessionAttribute() throws Exception {
        final Interceptor interceptor = interceptorAdding("x-rd-some-key", "some-value");
        final SessionHandler sessionHandler = new SimpleSessionHandler(asList(interceptor), SessionRoot.empty());

        final SessionRoot session = sessionHandler.initialize(mock(RequestContext.class)).toCompletableFuture().get();
        assertThat(session.get("some-key")).contains("some-value");
    }

    @Test
    public void shouldExecuteMultipleInterceptors() throws Exception {
        final Interceptor firstInterceptor = interceptorAdding("x-rd-first", "some-value");
        final Interceptor secondInterceptor = interceptorAdding("x-rd-second", "other-value");
        final SessionHandler sessionHandler =
            new SimpleSessionHandler(asList(firstInterceptor, secondInterceptor), SessionRoot.empty());

        final SessionRoot session = sessionHandler.initialize(mock(RequestContext.class)).toCompletableFuture().get();
        assertThat(session.get("first")).contains("some-value");
        assertThat(session.get("second")).contains("other-value");
    }

    private static Interceptor interceptorAdding(final String key, final String value) {
        return new Interceptor() {

            @Override
            public CompletionStage<SessionRoot> afterCreation(final SessionRoot session, final RequestContext context) {
                final Map<String, String> data = session.rawData();
                data.put(key, value);
                return CompletableFuture.completedFuture(SessionRoot.of(data));
            }
        };
    }

    private static class SimpleSessionHandler extends SessionHandler {

        private SessionRoot initial;

        protected SimpleSessionHandler(final List<Interceptor> interceptors, final SessionRoot initial) {
            super(interceptors);
            this.initial = initial;
        }

        @Override
        public <T> Response<T> writeTo(final Response<T> response, final Map<String, String> sessionData,
            final boolean dirty) {
            return response;
        }

        @Override
        protected SessionRoot obtainSession(final Request request) {
            return initial;
        }

    }

}
