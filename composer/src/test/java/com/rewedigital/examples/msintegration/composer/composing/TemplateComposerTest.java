package com.rewedigital.examples.msintegration.composer.composing;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

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
        final TemplateComposer composer =
            new TemplateComposer(aClientWithSimpleContent(content), Collections.emptyMap());

        final CompletableFuture<Composition> result = composer
            .compose("<rewe-digital-include path=\"http://mock/\"></rewe-digital-include>")
            .toCompletableFuture();

        assertThat(result)
            .isCompletedWithValueMatching(composition -> Objects.equal(composition.body(), content));
    }

    @Test
    public void appendsCSSLinksToHead() {
        final TemplateComposer composer =
            new TemplateComposer(aClientWithSimpleContent("", "css/link"), Collections.emptyMap());
        final CompletableFuture<Composition> result = composer
            .compose("<head></head><rewe-digital-include path=\"http://mock/\"></rewe-digital-include>")
            .toCompletableFuture();
        assertThat(result)
            .isCompletedWithValueMatching(composition -> {
                return Objects.equal(composition.body(), "<head><link rel=\"stylesheet\" href=\"css/link\" />\n" +
                    "</head>");
            });
    }

    @Test
    public void composesRecursiveTemplate() {
        final String innerContent = "some content";
        final TemplateComposer composer =
            new TemplateComposer(
                aClientWithConsecutiveContent("<rewe-digital-include path=\"http://mock/\"></rewe-digital-include>",
                    innerContent),
                Collections.emptyMap());
        final CompletableFuture<Composition> result = composer
            .compose("<rewe-digital-include path=\"http://mock/\"></rewe-digital-include>")
            .toCompletableFuture();
        assertThat(result)
            .isCompletedWithValueMatching(composition -> Objects.equal(composition.body(), innerContent));
    }


    private Client aClientWithSimpleContent(final String content) {
        return aClientWithSimpleContent(content, null);
    }

    private Client aClientWithSimpleContent(final String content, String cssLink) {
        Response<ByteString> response = contentResponse(content);
        if (cssLink != null) {
            response = response.withHeader(AssetLinkCompositionHander.STYLESHEET_HEADER, cssLink);
        }
        final Client client = mock(Client.class);
        when(client.send(any()))
            .thenReturn(CompletableFuture.completedFuture(response));
        return client;
    }

    private Client aClientWithConsecutiveContent(final String firstContent, final String... other) {
        final Client client = mock(Client.class);
        @SuppressWarnings("unchecked")
        final CompletableFuture<Response<ByteString>>[] otherResponses = Arrays.asList(other)
            .stream()
            .map(c -> CompletableFuture.completedFuture(contentResponse(c)))
            .collect(Collectors.toList()).toArray(new CompletableFuture[0]);

        when(client.send(any())).thenReturn(CompletableFuture.completedFuture(contentResponse(firstContent)),
            otherResponses);
        return client;
    }

    private Response<ByteString> contentResponse(final String content) {
        return Response
            .forPayload(ByteString.encodeUtf8("<rewe-digital-content>" + content + "</rewe-digital-content>"));
    }
}
