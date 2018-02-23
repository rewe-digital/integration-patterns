package com.rewedigital.examples.msintegration.composer.composing;

import java.util.Map;

import com.rewedigital.examples.msintegration.composer.session.SessionRoot;
import com.spotify.apollo.Client;
import com.typesafe.config.Config;

public class ComposerFactory {

    private final ComposerHtmlConfiguration configuration;

    public ComposerFactory(final Config configuration) {
        this.configuration = ComposerHtmlConfiguration.fromConfig(configuration);
    }

    public TemplateComposer build(final Client client, final Map<String, Object> parsedPathArguments,
        final SessionRoot session) {
        return new AttoParserBasedComposer(new ValidatingContentFetcher(client, parsedPathArguments, session), session,
            configuration);
    }

}
