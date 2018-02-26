package com.rewedigital.examples.msintegration.composer.architecture;

import static guru.nidi.codeassert.junit.CodeAssertMatchers.hasNoCycles;
import static guru.nidi.codeassert.junit.CodeAssertMatchers.matchesRulesExactly;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import guru.nidi.codeassert.config.AnalyzerConfig;
import guru.nidi.codeassert.dependency.DependencyAnalyzer;
import guru.nidi.codeassert.dependency.DependencyResult;
import guru.nidi.codeassert.dependency.DependencyRule;
import guru.nidi.codeassert.dependency.DependencyRuler;
import guru.nidi.codeassert.dependency.DependencyRules;

public class DependenciesTest {

    private final AnalyzerConfig config = AnalyzerConfig.maven().main();

    @Test
    public void noCycles() {
        assertThat(new DependencyAnalyzer(config).analyze(), hasNoCycles());
    }

    @Test
    public void clientDoesNotRelyOnComposing() {
        class ComRewedigitalExamplesMsintegrationComposer extends DependencyRuler {
            DependencyRule client, composing, configuration, parser, proxy, routing, session;

            @Override
            public void defineRules() {
                base().mayUse(base().allSub());

                client.mustNotUse(all());
                parser.mustNotUse(all());
                configuration.mustNotUse(all());

                composing.mayUse(client, parser, session);
                proxy.mayUse(composing, routing, session);
                routing.mayUse(composing, session);
                session.mustNotUse(client, parser, composing, proxy);
            }
        }

        final DependencyRules rules = DependencyRules.denyAll()
            .withRelativeRules(new ComRewedigitalExamplesMsintegrationComposer())
            .withExternals("java.*", "org.*", "net.*", "com.spotify.*", "com.google.*",
                "com.damnhandy.*", "okio", "com.fasterxml.*", "com.typesafe.*", "io.jsonwebtoken", "io.jsonwebtoken.*",
                "javax.*");

        final DependencyResult result = new DependencyAnalyzer(config).rules(rules).analyze();

        assertThat(result, matchesRulesExactly());
    }
}
