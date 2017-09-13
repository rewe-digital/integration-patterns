package com.rewedigital.examples.msintegration.composer.proxy;

import java.util.Map;

import com.rewedigital.examples.msintegration.composer.composingold.TemplateComposer;
import com.spotify.apollo.Client;

public class ComposerFactory {

    public TemplateComposer build(final Client client, final Map<String, Object> parsedPathArguments) {
        return new TemplateComposer(client, parsedPathArguments);
    }

}
