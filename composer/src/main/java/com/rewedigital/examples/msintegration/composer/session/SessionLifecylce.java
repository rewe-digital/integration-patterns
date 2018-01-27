package com.rewedigital.examples.msintegration.composer.session;

import java.util.Map;

import com.spotify.apollo.Request;
import com.spotify.apollo.Response;

public interface SessionLifecylce extends Session.Serializer {

    public static SessionLifecylce noSession() {
        return new SessionLifecylce() {

            @Override
            public <T> Response<T> writeTo(final Response<T> response, final Map<String, String> sessionData, final boolean dirty) {
                return response;
            }

            @Override
            public Session buildSession(final Request request) {
                return Session.empty();
            }
        };
    }

    Session buildSession(final Request request);

}
