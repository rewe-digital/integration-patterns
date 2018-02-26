package com.rewedigital.examples.msintegration.composer.session;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.spotify.apollo.RequestContext;
import com.typesafe.config.Config;

public class LocalSessionIdInterceptor implements SessionHandler.Interceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(LocalSessionIdInterceptor.class);

    private final long ttl;
    private final long renewAfter;

    public LocalSessionIdInterceptor(final Config args) {
        this.ttl = args.getLong("ttl");
        this.renewAfter = args.getLong("renew-after");
    }

    @Override
    public CompletionStage<SessionRoot> afterCreation(final SessionRoot session, final RequestContext context) {
        return CompletableFuture.completedFuture(withId(withExpiration(session, context)));
    }

    private SessionRoot withId(final SessionRoot session) {
        return session.getId()
            .map(id -> session)
            .orElse(session.withId(newSessionId()));
    }

    private String newSessionId() {
        return UUID.randomUUID().toString();
    }

    private SessionRoot withExpiration(final SessionRoot session, final RequestContext context) {
        Map<String, String> data = session.rawData();
        final Optional<Long> expiresAt =
            Optional.ofNullable(data.get("expires-at")).map(this::parseExpiresAt);

        if (isExpired(expiresAt, context)) {
            data = new HashMap<>();
        }

        final long newExpiresAt = expiration(expiresAt, context);
        data.put("expires-at", Long.toString(newExpiresAt));
        final boolean dirty = expiresAt.map(e -> e != newExpiresAt).orElse(true);
        return SessionRoot.of(data, dirty);
    }

    private boolean isExpired(final Optional<Long> expiresAt, final RequestContext context) {
        return expiresAt.map(exp -> context.metadata().arrivalTime().getEpochSecond() > exp).orElse(false);
    }


    private long expiration(final Optional<Long> expiresAt, final RequestContext context) {
        final long arrivalTime = context.metadata().arrivalTime().getEpochSecond();
        return expiresAt
            .filter(exp -> exp - arrivalTime > renewAfter)
            .orElse(arrivalTime + ttl);
    }

    private long parseExpiresAt(final String s) {
        try {
            return Long.parseLong(s);
        } catch (final NumberFormatException ex) {
            LOGGER.error("maleformatted expires-at: {} - expireing session", s, ex);
            return -1;
        }
    }
}
