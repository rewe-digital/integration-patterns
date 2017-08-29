package com.rewedigital.examples.msintegration.composer;

import java.util.Objects;

import com.rewedigital.examples.msintegration.composer.proxy.ComposingHandler;
import com.rewedigital.examples.msintegration.composer.proxy.RequestBuilder;
import com.rewedigital.examples.msintegration.composer.routing.BackendRouting;
import com.rewedigital.examples.msintegration.composer.routing.StaticRoutes;
import com.spotify.apollo.Environment;
import com.spotify.apollo.core.Service;
import com.spotify.apollo.http.client.HttpClientModule;
import com.spotify.apollo.httpservice.HttpService;
import com.spotify.apollo.httpservice.LoadingException;
import com.spotify.apollo.route.Route;

public class Application {

    public static void main(final String[] args) throws LoadingException {
        final ComposerInitializer initializer =
            new ComposerInitializer(
                new ComposingHandler(new BackendRouting(StaticRoutes.routes()), new RequestBuilder()));
        final Service service =
            HttpService
                .usingAppInit(initializer::init, "composer")
                .withModule(HttpClientModule.create())
                .build();
        HttpService.boot(service, args);
    }

    private static class ComposerInitializer {

        private final ComposingHandler handler;

        private ComposerInitializer(final ComposingHandler handler) {
            this.handler = Objects.requireNonNull(handler);
        }

        private void init(final Environment environment) {
            environment.routingEngine()
                .registerAutoRoute(Route.async("GET", "/<path:path>", rc -> handler.execute(rc)))
                .registerAutoRoute(Route.async("HEAD", "/<path:path>", rc -> handler.execute(rc)))
                .registerAutoRoute(Route.async("POST", "/<path:path>", rc -> handler.execute(rc)))
                .registerAutoRoute(Route.async("PUT", "/<path:path>", rc -> handler.execute(rc)))
                .registerAutoRoute(Route.async("DELETE", "/<path:path>", rc -> handler.execute(rc)))
                .registerAutoRoute(Route.async("TRACE", "/<path:path>", rc -> handler.execute(rc)))
                .registerAutoRoute(Route.async("OPTIONS", "/<path:path>", rc -> handler.execute(rc)))
                .registerAutoRoute(Route.async("PATCH", "/<path:path>", rc -> handler.execute(rc)));
        }

    }
}
