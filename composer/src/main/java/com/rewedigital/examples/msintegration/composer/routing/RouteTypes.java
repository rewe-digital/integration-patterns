package com.rewedigital.examples.msintegration.composer.routing;

import java.util.Objects;

import com.rewedigital.examples.msintegration.composer.composing.ComposerFactory;

public class RouteTypes {

    private final ComposerFactory composerFactory;
    private final SessionAwareProxyClient templateClient;

    public RouteTypes(final ComposerFactory composerFactory, final SessionAwareProxyClient templateClient) {
        this.templateClient = Objects.requireNonNull(templateClient);
        this.composerFactory = Objects.requireNonNull(composerFactory);
    }

    public ProxyRoute proxy() {
        return new ProxyRoute(templateClient);
    }

    public TemplateRoute template() {
        return new TemplateRoute(templateClient, composerFactory);
    }

}
