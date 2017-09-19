package com.rewedigital.examples.msintegration.composer.proxy;

import java.util.Map;

import com.rewedigital.examples.msintegration.composer.composing.AttoParserBasedComposer;
import com.rewedigital.examples.msintegration.composer.composing.ComposerHtmlConfiguration;
import com.rewedigital.examples.msintegration.composer.composing.TemplateComposer;
import com.spotify.apollo.Client;
import com.typesafe.config.Config;

public class ComposerFactory {

    private final ComposerHtmlConfiguration configuration;

    public ComposerFactory(Config configuration) {
        this.configuration = ComposerHtmlConfiguration.fromConfig(configuration);
    }

    public TemplateComposer build(final Client client, final Map<String, Object> parsedPathArguments) {
        return new AttoParserBasedComposer(new ValidatingContentFetcher(client, parsedPathArguments), configuration);
    }

}
