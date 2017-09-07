package com.rewedigital.examples.msintegration.composer.proxy;

import com.rewedigital.examples.msintegration.composer.composing.TemplateComposer;
import com.spotify.apollo.Client;

public class ComposerFactory {

    public TemplateComposer build(final Client client) {
        return new TemplateComposer(client);
    }

}
