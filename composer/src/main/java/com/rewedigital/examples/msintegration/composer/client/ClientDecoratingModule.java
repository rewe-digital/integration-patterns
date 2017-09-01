package com.rewedigital.examples.msintegration.composer.client;

import com.google.inject.multibindings.Multibinder;
import com.spotify.apollo.environment.ClientDecorator;
import com.spotify.apollo.module.AbstractApolloModule;

public class ClientDecoratingModule extends AbstractApolloModule {

    private final ClientDecorator clientDecorator;

    public ClientDecoratingModule(final ClientDecorator clientDecorator) {
        this.clientDecorator = clientDecorator;
    }

    @Override
    protected void configure() {
        Multibinder.newSetBinder(binder(), ClientDecorator.class)
            .addBinding().toInstance(clientDecorator);
    }

    @Override
    public String getId() {
        return "client-decorator";
    }

}