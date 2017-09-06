package com.rewedigital.examples.msintegration.composer.proxy;

import com.rewedigital.examples.msintegration.composer.composing.Composer;
import com.spotify.apollo.Client;

public class ComposerFactory {

    public Composer build(final Client client) {
        return new Composer(client);
    }

}
