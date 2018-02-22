package com.rewedigital.examples.msintegration.composer.session;

import java.util.UUID;

import com.typesafe.config.Config;

public class LocalSessionIdInterceptor implements SessionLifecycle.Interceptor {
    
    public LocalSessionIdInterceptor(final Config args) {
    }

    @Override
    public Session afterCreation(final Session session) {
        return session.getId().map(id -> session).orElse(session.withId(newSessionId()));
    }

    private String newSessionId() {
        return UUID.randomUUID().toString();
    }

}
