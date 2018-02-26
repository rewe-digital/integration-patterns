package com.rewedigital.examples.msintegration.composer.session;

import java.util.Objects;
import java.util.function.Function;

import com.spotify.apollo.Response;

public class ResponseWithSession<T> {

    private final Response<T> response;
    private final SessionRoot session;

    public ResponseWithSession(final Response<T> response, final SessionRoot session) {
        this.response = Objects.requireNonNull(response);
        this.session = Objects.requireNonNull(session);
    }

    public Response<T> response() {
        return response;
    }

    public SessionRoot session() {
        return session;
    }
    
    public <S> ResponseWithSession<S> transform(final Function<Response<T>, Response<S>> transformation) {
        return new ResponseWithSession<S>(transformation.apply(response), session);
    }

    public Response<T> writeSessionToResponse(final SessionRoot.Serializer serializer) {
        return session.writeTo(response, serializer);
    }
}
