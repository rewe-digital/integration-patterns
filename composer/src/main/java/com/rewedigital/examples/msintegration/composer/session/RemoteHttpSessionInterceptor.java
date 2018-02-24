package com.rewedigital.examples.msintegration.composer.session;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.CompletionStage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spotify.apollo.Request;
import com.spotify.apollo.RequestContext;
import com.spotify.apollo.Response;
import com.typesafe.config.Config;

import okio.ByteString;

public class RemoteHttpSessionInterceptor implements SessionHandler.Interceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(RemoteHttpSessionInterceptor.class);

    private final String url;
    private final ObjectMapper objectMapper;

    public RemoteHttpSessionInterceptor(final Config args) {
        this.url = args.getString("url");
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public CompletionStage<SessionRoot> afterCreation(final SessionRoot session, final RequestContext context) {
        return context.requestScopedClient()
            .send(Request.forUri(url, "POST").withPayload(toJsonPayload(session)))
            .thenApply(response -> handleResponse(response, session));
    }

    private SessionRoot handleResponse(final Response<ByteString> response, final SessionRoot session) {
        final Map<String, String> responseData =
            response.payload()
                .map(p -> p.utf8()).map(p -> parse(p))
                .orElse(Collections.emptyMap());

        final SessionData mergedData = session.data().mergedWith(new SessionData(responseData));
        return SessionRoot.of(mergedData.rawData());
    }

    private ByteString toJsonPayload(final SessionRoot session) {
        try {
            final String payload = objectMapper.writeValueAsString(session.rawData());
            return ByteString.encodeUtf8(payload);
        } catch (final JsonProcessingException e) {
            LOGGER.error("could not write json payload for session");
            return ByteString.encodeUtf8("{}");
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, String> parse(final String payload) {
        try {
            return objectMapper.readValue(payload, Map.class);
        } catch (final IOException e) {
            LOGGER.error("Could not read json response {}", payload);
            return Collections.emptyMap();
        }
    }
}
