package com.rewedigital.examples.msintegration.composer.composing;

import java.io.StringWriter;
import java.util.List;

import org.attoparser.IMarkupParser;
import org.attoparser.MarkupParser;
import org.attoparser.ParseException;
import org.attoparser.config.ParseConfiguration;

import com.google.common.base.Throwables;
import com.rewedigital.examples.msintegration.composer.composing.parser.ContentContributorSelectorHandler;
import com.spotify.apollo.Environment;

public class Composer {

    private final IMarkupParser parser = new MarkupParser(ParseConfiguration.htmlConfiguration());
    private final Environment environment;

    public Composer(final Environment environment) {
        this.environment = environment;
    }

    public String compose(final String baseTemplate) {

        // Parse
        final ContentContributorSelectorHandler handler = new ContentContributorSelectorHandler();
        try {
            parser.parse(baseTemplate, handler);
        } catch (final ParseException e) {
            Throwables.propagate(e);
        }

        final List<IncludedService> includedServices = handler.includedServices();

        // Start the minions
        for (final IncludedService service : includedServices) {
            service.fetchContent(environment.client());
        }

        final StringWriter writer = new StringWriter(baseTemplate.length());
        int start = 0;
        for (final IncludedService service : includedServices) {
            start = service.inject(writer, baseTemplate, start);
        }
        writer.write(baseTemplate, start, baseTemplate.length() - start);

        return writer.toString();
    }


}
