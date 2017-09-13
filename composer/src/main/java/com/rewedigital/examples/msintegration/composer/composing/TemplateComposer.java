package com.rewedigital.examples.msintegration.composer.composing;

import static com.rewedigital.examples.msintegration.composer.util.StreamUtil.flatten;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import org.attoparser.AbstractMarkupHandler;
import org.attoparser.IMarkupHandler;

import com.damnhandy.uri.template.UriTemplate;
import com.spotify.apollo.Client;
import com.spotify.apollo.Request;
import com.spotify.apollo.Response;
import com.spotify.apollo.Status;

public class TemplateComposer {

    private final Client client;
    private final Map<String, Object> parsedPathArguments;

    public TemplateComposer(final Client client, final Map<String, Object> parsedPathArguments) {
        this.client = requireNonNull(client);
        this.parsedPathArguments = requireNonNull(parsedPathArguments);
    }

    public CompletableFuture<Response<String>> compose(final Response<String> templateResponse) {
        return composeTemplate(templateResponse);
    }

    private CompletableFuture<Response<String>> composeTemplate(final Response<String> templateResponse) {
        final String template = bodyOf(templateResponse);

        final IncludeMarkupHandler includeHandler = new IncludeMarkupHandler(emptyMarkupHandler());
        final ContentMarkupHandler contentHandler = new AllContentMarkupHandler(includeHandler, template.length());
        final AssetMarkupHandler assetHandler = new AssetMarkupHandler(contentHandler);
        return doCompose(template, includeHandler, contentHandler, assetHandler)
            .thenApply(c -> c.toResponse());
    }


    public CompletableFuture<Composition> composeContent(final Response<String> templateResponse) {
        final String template = bodyOf(templateResponse);

        final IncludeMarkupHandler includeHandler = new IncludeMarkupHandler(emptyMarkupHandler());
        final ContentMarkupHandler contentHandler = new ContentMarkupHandler(includeHandler);
        final AssetMarkupHandler assetHandler = new AssetMarkupHandler(contentHandler);
        return doCompose(template, includeHandler, contentHandler, assetHandler);
    }

    private CompletableFuture<Composition> doCompose(final String template, final IncludeMarkupHandler includeHandler,
        final ContentMarkupHandler contentHandler, final AssetMarkupHandler assetHandler) {
        Parser.PARSER.parseIncludes(template, assetHandler);

        final List<String> assetLinks = assetHandler.assetLinks();
        final ContentRange contentRange = contentHandler.contentRange();
        final List<IncludedService> includes = includeHandler.includedServices();

        Stream<CompletableFuture<Composition>> composition = includes.stream()
            .filter(s -> s.isInRage(contentRange))
            .map(s -> s.fetch(contentFetcher())
                .thenCompose(r -> r.compose(this)));
        return flatten(composition)
            .thenApply(c -> new Composition(template, contentRange, assetLinks, c.collect(toList())));
    }


    private static IMarkupHandler emptyMarkupHandler() {
        return new AbstractMarkupHandler() {};
    }

    private static String bodyOf(Response<String> templateResponse) {
        return templateResponse.payload().orElse("");
    }

    private ContentFetcher contentFetcher() {
        return new ContentFetcher() {

            @Override
            public CompletableFuture<Response<String>> fetch(String path) {
                if (path == null || path.trim().isEmpty()) {
                    // LOGGER.warn("Empty path attribute in include found");
                    return CompletableFuture.completedFuture(Response.forStatus(Status.OK).withPayload(""));
                }

                final String expandedPath = UriTemplate.fromTemplate(path).expand(parsedPathArguments);
                return client.send(Request.forUri(expandedPath, "GET"))
                    .thenApply(response -> response.withPayload(response.payload().map(p -> p.utf8()).orElse("")))
                    .toCompletableFuture();
            }
        };
    }
}
