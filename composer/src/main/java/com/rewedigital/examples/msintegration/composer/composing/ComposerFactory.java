package com.rewedigital.examples.msintegration.composer.composing;

import java.util.Map;

import com.rewedigital.examples.msintegration.composer.session.Session;
import com.spotify.apollo.Client;
import com.typesafe.config.Config;

public class ComposerFactory {

    private final ComposerHtmlConfiguration configuration;

    public ComposerFactory(Config configuration) {
        this.configuration = ComposerHtmlConfiguration.fromConfig(configuration);
    }

    public TemplateComposer build(final Client client, final Map<String, Object> parsedPathArguments,
        final Session session) {
        return new AttoParserBasedComposer(new ValidatingContentFetcher(client, parsedPathArguments, session), session,
            configuration);
    }

}
