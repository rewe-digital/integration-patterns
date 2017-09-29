package com.rewedigital.examples.msintegration.composer.session;

import org.junit.Test;

import com.rewedigital.examples.msintegration.composer.configuration.DefaultConfiguration;
import com.typesafe.config.ConfigException;

public class SessionConfigurationTest {

    @Test
    public void allConfigParametersAreCoveredByDefaultConfig() {
        SessionConfiguration.fromConfig(DefaultConfiguration.defaultConfiguration().getConfig("composer.session"));
    }

    @Test(expected = ConfigException.Missing.class)
    public void configFailsOnMissingParameters() {
        SessionConfiguration.fromConfig(DefaultConfiguration.defaultConfiguration().getConfig("composer.html"));
    }

}
