package com.rewedigital.examples.msintegration.composer.session;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;

import java.io.IOException;
import java.net.HttpCookie;
import java.security.Key;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rewedigital.examples.msintegration.composer.session.Session.Serializer;
import com.spotify.apollo.Request;
import com.spotify.apollo.Response;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.crypto.MacProvider;

public class CookieBasedSessionSerializer implements Serializer {
    private static final Logger LOGGER = LoggerFactory.getLogger(CookieBasedSessionSerializer.class);

    private final String sessionCookieName = "rdsession";
    private final Key signingKey = MacProvider.generateKey();// FIXME TV read from config
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final TypeReference<HashMap<String, String>> typeRef = new TypeReference<HashMap<String, String>>() {};
    private final JwtParser parser = Jwts.parser().setSigningKey(signingKey);

    @Override
    public Map<String, String> readFrom(final Request request) {
        return request.header("Cookie")
            .map(HttpCookie::parse)
            .orElse(emptyList())
            .stream()
            .filter(this::isSessionCookie)
            .map(this::parseSessionCookie)
            .map(this::extractSessionValues)
            .findFirst()
            .orElse(emptyMap());
    }

    @Override
    public <T> Response<T> writeTo(final Response<T> response, final Map<String, String> session) {
        final String value = Jwts.builder()
            .claim("payload", writeSessionValues(session))
            .signWith(SignatureAlgorithm.HS512, signingKey)
            .compact();
        final HttpCookie cookie = new HttpCookie(sessionCookieName, value);
        return response.withHeader("Cookie", cookie.toString());
    }

    private boolean isSessionCookie(final HttpCookie cookie) {
        return sessionCookieName.equalsIgnoreCase(cookie.getName());
    }

    private String parseSessionCookie(final HttpCookie value) {
        try {
            Claims body = parser.parseClaimsJws(value.getValue()).getBody();
            return body.get("payload", String.class);
        } catch (JwtException e) {
            LOGGER.warn("error parsing jwt cookie", e);
            return "{}";
        }
    }

    private String writeSessionValues(final Map<String, String> session) {
        try {
            return objectMapper.writeValueAsString(session);
        } catch (JsonProcessingException e) {
            LOGGER.warn("error writing session payload", e);
            return "{}";
        }
    }

    private Map<String, String> extractSessionValues(final String session) {
        try {
            return objectMapper.readValue(session, typeRef);
        } catch (IOException e) {
            LOGGER.warn("error parsing session payload", e);
            return emptyMap();
        }
    }
}
