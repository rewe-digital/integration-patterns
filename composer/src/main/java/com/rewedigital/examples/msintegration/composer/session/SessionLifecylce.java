package com.rewedigital.examples.msintegration.composer.session;

import java.util.Map;

import com.spotify.apollo.RequestContext;
import com.spotify.apollo.Response;

public interface SessionLifecylce extends Session.Serializer {

    public static SessionLifecylce noSession() {
        return new SessionLifecylce() {

            @Override
            public <T> Response<T> writeTo(final Response<T> response, final Map<String, String> sessionData, boolean dirty) {
                return response;
            }

            @Override
            public Session buildSession(final RequestContext requestContext) {
                return Session.empty();
            }
        };
    }

    Session buildSession(final RequestContext requestContext);

}
