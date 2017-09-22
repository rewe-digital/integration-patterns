package com.rewedigital.examples.msintegration.composer.session;

import java.util.Collections;
import java.util.Map;

import com.spotify.apollo.Request;
import com.spotify.apollo.RequestContext;
import com.spotify.apollo.Response;

public interface SessionLifecylce extends Session.Serializer {

    public static SessionLifecylce noSession() {
        return new SessionLifecylce() {

            @Override
            public Map<String, String> readFrom(final Request request) {
                return Collections.emptyMap();
            }

            @Override
            public <T> Response<T> writeTo(final Response<T> response, final Map<String, String> sessionData) {
                return response;
            }

            @Override
            public Session newSession(final RequestContext requestContext) {
                return Session.empty();
            }
        };
    }

    Session newSession(final RequestContext requestContext);

}
