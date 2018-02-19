package com.rewedigital.examples.msintegration.composer.session;

import java.util.UUID;

import com.typesafe.config.Config;

public class SessionIdInterceptor implements SessionLifecycle.Interceptor {
    
    public SessionIdInterceptor(final Config args) {
    }

    @Override
    public Session afterCreation(final Session session) {
        return session.getId().map(id -> session).orElse(session.withId(newSessionId()));
    }

    private String newSessionId() {
        return UUID.randomUUID().toString();
    }

}
