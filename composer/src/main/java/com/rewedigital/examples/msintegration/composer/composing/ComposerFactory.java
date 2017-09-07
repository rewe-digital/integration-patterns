package com.rewedigital.examples.msintegration.composer.composing;

import com.spotify.apollo.Client;

public class ComposerFactory {

    public TemplateComposer build(final Client client) {
        return new TemplateComposer(client);
    }

}
