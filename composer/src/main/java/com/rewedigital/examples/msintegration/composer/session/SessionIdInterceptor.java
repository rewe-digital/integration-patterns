package com.rewedigital.examples.msintegration.composer.session;

import java.util.UUID;

public class SessionIdInterceptor implements SessionLifecycle.Interceptor {

    @Override
    public Session afterCreation(final Session session) {
        return session.getId().map(id -> session).orElse(session.withId(newSessionId()));
    }

    private String newSessionId() {
        return UUID.randomUUID().toString();
    }

}
