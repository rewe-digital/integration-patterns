package com.rewedigital.examples.msintegration.composer.composing;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.concurrent.CompletableFuture;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import com.google.common.base.Objects;
import com.rewedigital.examples.msintegration.composer.composing.parser.Composition;
import com.spotify.apollo.Client;
import com.spotify.apollo.Response;

import okio.ByteString;

public class TemplateComposerTest {

    @Test
    public void composesSimpleTemplate() {
        final String content = "content";
        final TemplateComposer composer = new TemplateComposer(aClientWithSimpleContent(content));

        final CompletableFuture<Composition> result = composer
            .compose("<rewe-digital-include path=\"http://mock/\"></rewe-digital-include>")
            .toCompletableFuture();

        assertThat(result)
            .isCompletedWithValueMatching(composition -> Objects.equal(composition.body(), content));
    }

    @Test
    public void appendsCSSLinksToHead() {
        final TemplateComposer composer = new TemplateComposer(aClientWithSimpleContent("", "css/link"));
        final CompletableFuture<Composition> result = composer
            .compose("<head></head><rewe-digital-include path=\"http://mock/\"></rewe-digital-include>")
            .toCompletableFuture();
        assertThat(result)
            .isCompletedWithValueMatching(composition -> {
                return Objects.equal(composition.body(), "<head><link rel=\"stylesheet\" href=\"css/link\" />\n" +
                    "</head>");
            });
    }

    private Client aClientWithSimpleContent(final String content) {
        return aClientWithSimpleContent(content, null);
    }

    private Client aClientWithSimpleContent(final String content, String cssLink) {
        Response<ByteString> response =
            Response.forPayload(ByteString.encodeUtf8("<rewe-digital-content>" + content + "</rewe-digital-content>"));
        if (cssLink != null) {
            response = response.withHeader(AssetLinkCompositionHander.STYLESHEET_HEADER, cssLink);
        }
        final Client client = mock(Client.class);
        when(client.send(any()))
            .thenReturn(CompletableFuture.completedFuture(response));
        return client;
    }
}
