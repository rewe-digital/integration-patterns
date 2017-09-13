package com.rewedigital.examples.msintegration.composer.composing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.junit.Test;

import com.spotify.apollo.Client;
import com.spotify.apollo.Response;

import okio.ByteString;

public class TemplateComposerTest {

    @Test
    public void IgnoresIncludeWhenPathIsMissing() throws Exception {
        final TemplateComposer composer =
            new TemplateComposer(aClientWithSimpleContent("should not be included"), Collections.emptyMap());

        final Response<String> result = composer
            .compose(r("template <rewe-digital-include></rewe-digital-include> content")).get();

        assertThat(result.payload().get()).isEqualTo("template  content");
    }

    @Test
    public void composesSimpleTemplate() throws Exception {
        final String content = "content";
        final TemplateComposer composer =
            new TemplateComposer(aClientWithSimpleContent(content), Collections.emptyMap());

        final Response<String> result = composer
            .compose(r(
                "template content <rewe-digital-include path=\"http://mock/\"></rewe-digital-include> more content"))
            .get();

        assertThat(result.payload().get()).isEqualTo("template content " + content + " more content");
    }

    @Test
    public void appendsCSSLinksToHead() throws Exception {
        final TemplateComposer composer =
            new TemplateComposer(
                aClientWithSimpleContent("",
                    "<link href=\"css/link\" data-rd-options=\"include\" rel=\"stylesheet\"/>"),
                Collections.emptyMap());
        final Response<String> result = composer
            .compose(r("<head></head><rewe-digital-include path=\"http://mock/\"></rewe-digital-include>")).get();
        assertThat(result.payload().get()).isEqualTo(
            "<head><link rel=\"stylesheet\" data-rd-options=\"include\" href=\"css/link\" />\n" +
                "</head>");
    }

    @Test
    public void composesRecursiveTemplate() throws Exception {
        final String innerContent = "some content";
        final TemplateComposer composer =
            new TemplateComposer(
                aClientWithConsecutiveContent(
                    "<rewe-digital-include path=\"http://other/mock/\"></rewe-digital-include>",
                    innerContent),
                Collections.emptyMap());
        final Response<String> result = composer
            .compose(r("<rewe-digital-include path=\"http://mock/\"></rewe-digital-include>"))
            .get();
        assertThat(result.payload().get()).isEqualTo(innerContent);
    }


    private Client aClientWithSimpleContent(final String content) {
        return aClientWithSimpleContent(content, "");
    }

    private Client aClientWithSimpleContent(final String content, final String head) {
        Response<ByteString> response = contentResponse(content, head);

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
            .map(c -> CompletableFuture.completedFuture(contentResponse(c, "")))
            .collect(Collectors.toList()).toArray(new CompletableFuture[0]);

        when(client.send(any())).thenReturn(CompletableFuture.completedFuture(contentResponse(firstContent, "")),
            otherResponses);
        return client;
    }

    private Response<ByteString> contentResponse(final String content, final String head) {
        return Response
            .forPayload(
                ByteString.encodeUtf8("<html><head>" + head + "</head><body><rewe-digital-content>" + content
                    + "</rewe-digital-content></body></html>"));
    }

    private Response<String> r(String body) {
        return Response.forPayload(body);
    }
}
