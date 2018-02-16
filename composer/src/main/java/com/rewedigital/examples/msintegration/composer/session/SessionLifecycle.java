package com.rewedigital.examples.msintegration.composer.session;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.spotify.apollo.Request;
import com.spotify.apollo.Response;

public abstract class SessionLifecycle implements Session.Serializer {

    public interface Interceptor {
        Session afterCreation(Session session);
    }

    public static SessionLifecycle noSession() {
        return new SessionLifecycle(Collections.emptyList()) {

            @Override
            public <T> Response<T> writeTo(final Response<T> response, final Map<String, String> sessionData,
                final boolean dirty) {
                return response;
            }

            @Override
            protected Session createSession(final Request request) {
                return Session.empty();
            }

            @Override
            public Session obtainSession(final Request request) {
                return createSession(request);
            }
        };
    }

    private final List<Interceptor> interceptors;

    protected SessionLifecycle(final List<Interceptor> interceptors) {
        this.interceptors = new LinkedList<>(interceptors);
    }

    protected abstract Session createSession(Request request);

    public Session obtainSession(final Request request) {
        final Session session = createSession(request);
        return runInterceptors(session);
    }

    private Session runInterceptors(final Session session) {
        return interceptors.stream().reduce(session, (s, i) -> i.afterCreation(s), (s, t) -> s.mergeWith(t));
    }
}
