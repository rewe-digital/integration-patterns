package com.rewedigital.examples.msintegration.composer.session;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.net.HttpCookie;
import java.security.Key;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spotify.apollo.Request;
import com.spotify.apollo.Response;
import com.typesafe.config.Config;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.crypto.MacProvider;

public class CookieBasedSessionLifecycle implements SessionLifecylce {

    public static class Factory implements SessionLifecycleFactory {

        private final SessionConfiguration configuration;

        public Factory(final Config configuration) {
            this.configuration = SessionConfiguration.fromConfig(configuration);
        }

        @Override
        public SessionLifecylce build() {
            if (!configuration.sessionEnabled()) {
                return SessionLifecylce.noSession();
            }
            return new CookieBasedSessionLifecycle(configuration);
        }
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(CookieBasedSessionLifecycle.class);
    private static final TypeReference<HashMap<String, String>> typeRef =
        new TypeReference<HashMap<String, String>>() {};
    private static final String PAYLOAD = "payload";
    private static final Key defaultSigningKey = MacProvider.generateKey();

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final SessionConfiguration configuration;
    private final JwtParser parser;
    private final SignatureAlgorithm algorithm;
    private final Key signingKey;


    public CookieBasedSessionLifecycle(final SessionConfiguration configuration) {
        this.configuration = requireNonNull(configuration);
        this.algorithm = SignatureAlgorithm.forName(configuration.signingAlgorithm());
        this.signingKey = defaultSigningKey;// FIXME TV read from config
        this.parser = Jwts.parser().setSigningKey(signingKey);
    }

    @Override
    public Session buildSession(final Request request) {
        final Map<String, String> sessionValues = readFrom(request);
        return Session.of(sessionValues);
    }

    private Map<String, String> readFrom(final Request request) {
        return request.header("Cookie")
            .map(this::readFromHeader)
            .orElse(emptyMap());
    }

    @Override
    public <T> Response<T> writeTo(final Response<T> response, final Map<String, String> session, final boolean dirty) {
        if (!dirty) {
            return response;
        }

        final String value = Jwts.builder()
            .claim(PAYLOAD, writeSessionValues(session))
            .signWith(algorithm, signingKey)
            .compact();
        final String cookie = buildSessionCookie(value);
        return response.withHeader("Set-Cookie", cookie);
    }

    private Map<String, String> readFromHeader(final String cookieHeader) {
        return parseCookieHeader(cookieHeader)
            .stream()
            .filter(this::isSessionCookie)
            .map(this::parseSessionCookie)
            .map(this::extractSessionValues)
            .findFirst()
            .orElse(emptyMap());
    }

    private List<HttpCookie> parseCookieHeader(final String cookieHeader) {
        try {
            return HttpCookie.parse(cookieHeader);
        } catch (final Exception e) {
            LOGGER.warn("erro parsing cookie headers, defaulting to empty session", e);
            return emptyList();
        }
    }

    private boolean isSessionCookie(final HttpCookie cookie) {
        final boolean result = configuration.cookieName().equalsIgnoreCase(cookie.getName());
        if (result) {
            LOGGER.info("session cookie found");
        }
        return result;
    }

    private String parseSessionCookie(final HttpCookie value) {
        try {
            return parser.parseClaimsJws(value.getValue()).getBody().get(PAYLOAD, String.class);
        } catch (final JwtException e) {
            LOGGER.warn("error parsing session cookie, defaulting to empty session", e);
            return "{}";
        }
    }

    private String writeSessionValues(final Map<String, String> session) {
        try {
            return objectMapper.writeValueAsString(session);
        } catch (final JsonProcessingException e) {
            LOGGER.warn("error writing session payload, defaulting to empty session", e);
            return "{}";
        }
    }

    private Map<String, String> extractSessionValues(final String session) {
        try {
            return objectMapper.readValue(session, typeRef);
        } catch (final IOException e) {
            LOGGER.warn("error parsing session payload, defaulting to empty session", e);
            return emptyMap();
        }
    }

    private String buildSessionCookie(final String value) {
        final HttpCookie httpCookie = new HttpCookie(configuration.cookieName(), value);
        httpCookie.setHttpOnly(true);
        httpCookie.setPath("/");
        // FIXME TV set domain?
        // FIXME TV set secure?
        return serialize(httpCookie);
    }

    // helper method to write a set-cookie header from a HttpCookie instance
    private static String serialize(final HttpCookie cookie) {
        final StringBuilder result = new StringBuilder();
        result.append(cookie.getName());
        result.append('=');
        result.append(cookie.getValue());

        if (cookie.getMaxAge() >= 0) {
            result.append("; max-age=").append(cookie.getMaxAge());
        }

        if (cookie.getDomain() != null) {
            result.append("; domain=");
            result.append(cookie.getDomain());
        }

        result.append("; path=").append(cookie.getPath());

        if (cookie.getSecure()) {
            result.append("; secure");
        }

        if (cookie.isHttpOnly()) {
            result.append("; httponly");
        }

        return result.toString();
    }
}
