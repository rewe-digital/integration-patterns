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
            DependencyRule client, composing, parser, proxy, routing, util;

            @Override
            public void defineRules() {
                base().mayUse(base().allSub());
                client.mayUse(util).mustNotUse(composing);
                parser.mayUse(util).mustNotUse(composing);
                composing.mayUse(client, parser, util).mustNotUse(proxy, routing);
                proxy.mayUse(composing, routing, util).mustNotUse(client);
            }
        }

        DependencyRules rules = DependencyRules.denyAll()
            .withRelativeRules(new ComRewedigitalExamplesMsintegrationComposer())
            .withExternals("java.*", "org.*", "net.*", "com.spotify.*", "com.google.*",
                "com.damnhandy.*", "okio");

        DependencyResult result = new DependencyAnalyzer(config).rules(rules).analyze();

        assertThat(result, matchesRulesExactly());

    }

}
