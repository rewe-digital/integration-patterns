package com.rewedigital.examples.msintegration.composer.session;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import com.spotify.apollo.RequestContext;
import com.typesafe.config.Config;

// TODO add grace time so we do not need to update expires-at on every request
public class LocalSessionIdInterceptor implements SessionHandler.Interceptor {
    private final long ttl;

    public LocalSessionIdInterceptor(final Config args) {
        this.ttl = args.getLong("ttl");
    }

    @Override
    public Session afterCreation(final Session session, final RequestContext context) {
        return withId(withExpiration(session, context));
    }

    private Session withId(final Session session) {
        return session.getId()
            .map(id -> session)
            .orElse(session.withId(newSessionId()));
    }

    private Session withExpiration(final Session session, final RequestContext context) {
        Map<String, String> data = session.rawData();
        final Optional<Long> expiresAt = Optional.ofNullable(data.get("expires-at")).map(this::parseExpiresAt);
        if (expiresAt.isPresent() && isExpired(expiresAt.get(), context)) {
            data = new HashMap<>();
        }
        data.put("expires-at", expiration(context));
        return Session.of(data);
    }

    private boolean isExpired(final Long expiresAt, final RequestContext context) {
        return context.metadata().arrivalTime().getEpochSecond() > expiresAt;
    }

    private String newSessionId() {
        return UUID.randomUUID().toString();
    }

    private String expiration(final RequestContext context) {
        return Long.toString(context.metadata().arrivalTime().getEpochSecond() + ttl);
    }

    private long parseExpiresAt(final String s) {
        try {
            return Long.parseLong(s);
        } catch (final Exception ex) {
            // FIXME log
            return -1;
        }
    }

}
