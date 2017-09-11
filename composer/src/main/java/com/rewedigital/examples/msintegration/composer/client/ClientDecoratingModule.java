package com.rewedigital.examples.msintegration.composer.client;

import static java.util.Objects.requireNonNull;

import java.util.stream.Stream;

import com.google.inject.multibindings.Multibinder;
import com.spotify.apollo.environment.ClientDecorator;
import com.spotify.apollo.module.AbstractApolloModule;

public class ClientDecoratingModule extends AbstractApolloModule {

    private ClientDecorator[] clientDecorators;

    public ClientDecoratingModule(final ClientDecorator... clientDecorators) {
        this.clientDecorators = requireNonNull(clientDecorators);
    }

    @Override
    protected void configure() {
        Stream.of(clientDecorators).forEach(this::bindClientDecorator);
    }

    private void bindClientDecorator(ClientDecorator clientDecorator) {
        Multibinder.newSetBinder(binder(), ClientDecorator.class).addBinding().toInstance(clientDecorator);
    }

    @Override
    public String getId() {
        return "client-decorator";
    }

}
