package com.rewedigital.examples.msintegration.composer.session;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.spotify.apollo.Request;
import com.spotify.apollo.Response;

public class CookieBasedSessionLifecycleTest {

    @Test
    public void shouldWriteAndReadCookie() {
        final CookieBasedSessionLifecycle serializer = new CookieBasedSessionLifecycle(configuration());
        final String cookieHeader =
            serializer.writeTo(Response.ok(), session("x-rd-key", "value")).header("Set-Cookie").get();
        final Map<String, String> session = serializer.readFrom(Request.forUri("/").withHeader("Cookie", cookieHeader));
        assertEquals("value", session.get("x-rd-key"));
    }

    private SessionConfiguration configuration() {
        return new SessionConfiguration(true, "sessioncookie", "HS512");
    }

    private static Map<String, String> session(final String key, final String value) {
        final HashMap<String, String> result = new HashMap<>();
        result.put(key, value);
        return result;
    }

}
