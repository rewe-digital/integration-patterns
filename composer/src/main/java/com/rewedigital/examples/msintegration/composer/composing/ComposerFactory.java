package com.rewedigital.examples.msintegration.composer.composing;

import java.util.Map;

import com.spotify.apollo.Client;

public class ComposerFactory {


    public Composer build(final Client client, final Map<String, Object> parsedPathArguments) {
        return new Composer(client, parsedPathArguments);
    }

}
