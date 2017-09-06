package com.rewedigital.examples.msintegration.composer.composing;

import com.spotify.apollo.Client;

public class ComposerFactory {

    public Composer build(final Client client) {
        return new Composer(client);
    }

}
