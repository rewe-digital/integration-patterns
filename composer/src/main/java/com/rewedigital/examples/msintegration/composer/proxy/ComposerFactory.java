package com.rewedigital.examples.msintegration.composer.proxy;

import java.util.Map;

import com.rewedigital.examples.msintegration.composer.composing.AttoParserBasedComposer;
import com.rewedigital.examples.msintegration.composer.composing.TemplateComposer;
import com.spotify.apollo.Client;

public class ComposerFactory {

    public TemplateComposer build(final Client client, final Map<String, Object> parsedPathArguments) {
        return new AttoParserBasedComposer(client, parsedPathArguments);
    }

}
