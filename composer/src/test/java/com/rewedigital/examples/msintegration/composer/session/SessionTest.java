package com.rewedigital.examples.msintegration.composer.session;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import com.spotify.apollo.Request;
import com.spotify.apollo.Response;
import com.spotify.apollo.Status;

public class SessionTest {

    @Test
    public void readsSessionDataFromResponse() {
        final Response<Object> response = Response.forStatus(Status.OK).withHeader("x-rd-some-key", "value")
            .withHeader("ignored-key", "ignored-value");

        final Session session = Session.of(response);

        assertThat(session.get("some-key")).contains("value");
        assertThat(session.get("ignored-key")).isNotPresent();
    }

    @Test
    public void ignoresCaseWhileSearchingForSessionKey() {
        final Response<Object> response = Response.forStatus(Status.OK).withHeader("X-RD-SOME-KEY", "value");

        final Session session = Session.of(response);

        assertThat(session.get("some-key")).contains("value");
    }

    @Test
    public void mergesTwoSessionsByMergingAllData() {
        final Session firstSession =
            Session.of(Response.forStatus(Status.OK).withHeader("x-rd-first-key", "first-value"));
        final Session secondSession =
            Session.of(Response.forStatus(Status.OK).withHeader("x-rd-second-key", "second-value"));

        final Session result = firstSession.mergeWith(secondSession);

        assertThat(result.get("first-key")).contains("first-value");
        assertThat(result.get("second-key")).contains("second-value");
    }

    @Test
    public void enrichesRequestWithSessionHeaders() {
        final Session session = Session.of(Response.forStatus(Status.OK).withHeader("x-rd-some-key", "value")
            .withHeader("x-rd-other-key", "other-value"));

        final Request request = session.enrich(Request.forUri("/some/path"));

        assertThat(request.header("x-rd-some-key")).contains("value");
        assertThat(request.header("x-rd-other-key")).contains("other-value");
    }

}
