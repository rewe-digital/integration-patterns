package com.rewedigital.examples.msintegration.composer;

import com.rewedigital.examples.msintegration.composer.composing.Composer;
import com.rewedigital.examples.msintegration.composer.proxy.ComposingHandler;
import com.rewedigital.examples.msintegration.composer.proxy.TemplateClient;
import com.rewedigital.examples.msintegration.composer.routing.BackendRouting;
import com.rewedigital.examples.msintegration.composer.routing.StaticBackendRoutes;
import com.spotify.apollo.Environment;
import com.spotify.apollo.core.Service;
import com.spotify.apollo.http.client.HttpClientModule;
import com.spotify.apollo.httpservice.HttpService;
import com.spotify.apollo.httpservice.LoadingException;
import com.spotify.apollo.route.Route;

public class Application {

    public static void main(final String[] args) throws LoadingException {

        final Service service =
            HttpService
                .usingAppInit(Initializer::init, "composer")
                .withModule(HttpClientModule.create())
                .build();

        HttpService.boot(service, args);
    }

    private static class Initializer {

        private static void init(final Environment environment) {
            final ComposingHandler handler =
                new ComposingHandler(
                    new BackendRouting(StaticBackendRoutes.routes()),
                    new TemplateClient(),
                    new Composer(environment));

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
