package com.rewedigital.examples.msintegration.composer.proxy;

import static com.rewedigital.examples.msintegration.composer.routing.StaticBackendRoutes.RouteType.PROXY;
import static com.rewedigital.examples.msintegration.composer.routing.StaticBackendRoutes.RouteType.TEMPLATE;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import org.junit.Test;

import com.rewedigital.examples.msintegration.composer.configuration.DefaultConfiguration;
import com.rewedigital.examples.msintegration.composer.routing.BackendRouting;
import com.rewedigital.examples.msintegration.composer.routing.BackendRouting.RouteMatch;
import com.rewedigital.examples.msintegration.composer.routing.StaticBackendRoutes;
import com.rewedigital.examples.msintegration.composer.routing.StaticBackendRoutes.Match;
import com.rewedigital.examples.msintegration.composer.session.Session;
import com.spotify.apollo.Client;
import com.spotify.apollo.Request;
import com.spotify.apollo.RequestContext;
import com.spotify.apollo.Response;
import com.spotify.apollo.Status;
import com.spotify.apollo.route.Rule;
import com.spotify.apollo.route.RuleRouter;

import okio.ByteString;

public class ComposingHandlerTest {

    private static final String SERVICE_RESPONSE = "<html>test</html>";

    @Test
    public void returnsResponseFromTemplateRoute() throws Exception {
        final ComposingRequestHandler handler =
            new ComposingRequestHandler(new BackendRouting(aRouter("/<path:path>", TEMPLATE)),
                StubTemplateClient.returning(Status.OK, SERVICE_RESPONSE), composerFactory());

        final Response<ByteString> response = handler.execute(aContext()).toCompletableFuture().get();

        assertThat(response.payload().get().utf8()).isEqualTo(SERVICE_RESPONSE);
    }

    @Test
    public void returnsDefaultResponseFromErrorOnTemplateRoute() throws Exception {
        final ComposingRequestHandler handler =
            new ComposingRequestHandler(new BackendRouting(aRouter("/<path:path>", TEMPLATE)),
                StubTemplateClient.returning(Status.BAD_REQUEST, ""), composerFactory());

        final Response<ByteString> response = handler.execute(aContext()).toCompletableFuture().get();
        assertThat(response.status()).isEqualTo(Status.INTERNAL_SERVER_ERROR);
    }

    @Test
    public void returnsErrorResponseFromProxyRoute() throws Exception {
        final ComposingRequestHandler handler =
            new ComposingRequestHandler(new BackendRouting(aRouter("/<path:path>", PROXY)),
                StubTemplateClient.returning(Status.BAD_REQUEST, ""), composerFactory());

        final Response<ByteString> response = handler.execute(aContext()).toCompletableFuture().get();
        assertThat(response.status()).isEqualTo(Status.BAD_REQUEST);

    }

    private RuleRouter<Match> aRouter(final String pattern, final StaticBackendRoutes.RouteType routeType) {
        final Rule<Match> sampleRule = Rule.fromUri(pattern, "GET", Match.of("http://target", routeType));
        return RuleRouter.of(singletonList(sampleRule));
    }

    private RequestContext aContext() {
        final Request request = mock(Request.class);
        when(request.uri()).thenReturn("http://uri.de/hello");
        when(request.service()).thenReturn(Optional.empty());
        when(request.method()).thenReturn("GET");
        final RequestContext context = mock(RequestContext.class);
        when(context.request()).thenReturn(request);
        Client client = mock(Client.class);
        when(client.send(any())).thenThrow(new RuntimeException());
        when(context.requestScopedClient()).thenReturn(client);
        return context;
    }


    private ComposerFactory composerFactory() {
        return new ComposerFactory(DefaultConfiguration.defaultConfiguration().getConfig("composer.html"));
    }

    private static class StubTemplateClient extends TemplateClient {

        public static TemplateClient returning(final Status status, final String responseBody) {
            return new TemplateClient() {
                @Override
                public CompletionStage<Response<ByteString>> getTemplate(final RouteMatch match,
                    final RequestContext context, final Session session) {
                    return CompletableFuture.completedFuture(Response.of(status, ByteString.encodeUtf8(responseBody)));
                }
            };
        }
    }

}
