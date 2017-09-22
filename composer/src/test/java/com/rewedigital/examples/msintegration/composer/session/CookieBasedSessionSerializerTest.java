package com.rewedigital.examples.msintegration.composer.session;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.spotify.apollo.Request;
import com.spotify.apollo.Response;

public class CookieBasedSessionSerializerTest {

    @Test
    public void shouldWriteAndReadCookie() {
        final CookieBasedSessionSerializer serializer = new CookieBasedSessionSerializer();
        final String cookieHeader =
            serializer.writeTo(Response.ok(), session("x-rd-key", "value")).header("Cookie").get();
        final Map<String, String> session = serializer.readFrom(Request.forUri("/").withHeader("Cookie", cookieHeader));
        assertEquals("value", session.get("x-rd-key"));
    }

    private static Map<String, String> session(final String key, final String value) {
        final HashMap<String, String> result = new HashMap<>();
        result.put(key, value);
        return result;
    }

}
