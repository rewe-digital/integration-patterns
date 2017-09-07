package com.rewedigital.examples.msintegration.composer.composing;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import com.google.common.base.Objects;
import com.rewedigital.examples.msintegration.composer.composing.parser.Composition;
import com.spotify.apollo.Client;
import com.spotify.apollo.Response;

import okio.ByteString;

public class ComposerTest {

    private static final String TEMPLATE = "<rewe-digital-include path=\"http://mock/\"></rewe-digital-include>";

    @Test
    public void composesSimpleTemplate() throws InterruptedException, ExecutionException {
        final Composer composer = new Composer(aClientWithSimpleContent());

        final CompletableFuture<Composition> content = composer.compose(TEMPLATE).toCompletableFuture();

        assertThat(content).isCompletedWithValueMatching(composition -> Objects.equal(composition.body(), "content"));
    }

    private Client aClientWithSimpleContent() {
        final Client client = mock(Client.class);
        when(client.send(any()))
            .thenReturn(CompletableFuture.completedFuture(
                Response.forPayload(ByteString.encodeUtf8("<rewe-digital-content>content</rewe-digital-content>"))));
        return client;
    }

}
