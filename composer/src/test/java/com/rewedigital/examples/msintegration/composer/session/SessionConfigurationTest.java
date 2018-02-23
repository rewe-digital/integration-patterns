package com.rewedigital.examples.msintegration.composer.session;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.rewedigital.examples.msintegration.composer.configuration.DefaultConfiguration;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigException;
import com.typesafe.config.ConfigFactory;

public class SessionConfigurationTest {

    @Test
    public void allConfigParametersAreCoveredByDefaultConfig() {
        SessionConfiguration.fromConfig(DefaultConfiguration.defaultConfiguration().getConfig("composer.session"));
    }

    @Test(expected = ConfigException.Missing.class)
    public void configFailsOnMissingParameters() {
        SessionConfiguration.fromConfig(DefaultConfiguration.defaultConfiguration().getConfig("composer.html"));
    }

    @Test
    public void canCreateInterceptorsBasedOnConfiguration() {
        final Config config = configWithInterceptors(sessionIdInterceptorConfig());
        final SessionConfiguration sessionConfiguration = SessionConfiguration.fromConfig(config);
        assertThat(sessionConfiguration.interceptors()).hasSize(1);
        assertThat(sessionConfiguration.interceptors().get(0)).isInstanceOf(LocalSessionIdInterceptor.class);
    }

    @Test(expected = ConfigException.Missing.class)
    public void failsOnMissingTypeInInterceptorsConfiguration() {
        final Config config = configWithInterceptors(new HashMap<>());
        SessionConfiguration.fromConfig(config);
    }

    @Test(expected = ConfigException.WrongType.class)
    public void failsIfInterceptorConfigIsNotAList() {
        final Config config = configWithBrokernInterceptorsConfig();
        SessionConfiguration.fromConfig(config);
    }

    @Test(expected = ConfigException.Generic.class)
    public void failsIfInterceptorCannotBeInstantiated() {
        final Config config = configWithInterceptors(interceptorConfig("doesnotexist", Collections.emptyMap()));
        SessionConfiguration.fromConfig(config);
    }

    private static Map<String, Object> sessionIdInterceptorConfig() {
        final Map<String, Object> args = new HashMap<>();
        args.put("ttl", 0);
        args.put("renew-after", 0);
        return interceptorConfig("com.rewedigital.examples.msintegration.composer.session.LocalSessionIdInterceptor",
            args);
    }

    private static Map<String, Object> interceptorConfig(final String type, final Map<String, Object> args) {
        final Map<String, Object> interceptorConfig = new HashMap<>();
        interceptorConfig.put("type", type);
        interceptorConfig.put("args", args);
        return interceptorConfig;
    }

    @SafeVarargs
    private static Config configWithInterceptors(final Map<String, Object>... interceptors) {
        final List<Map<String, Object>> interceptorsConfigList = Arrays.asList(interceptors);
        final Map<String, Object> interceptorsConfig = new HashMap<>();
        interceptorsConfig.put("composer.session.interceptors", interceptorsConfigList);
        return DefaultConfiguration
            .withDefaults(ConfigFactory.parseMap(interceptorsConfig, "test-interceptors-configuration"))
            .getConfig("composer.session");
    }


    private static Config configWithBrokernInterceptorsConfig() {
        final Map<String, Object> interceptorsConfig = new HashMap<>();
        interceptorsConfig.put("composer.session.interceptors", "test");
        return DefaultConfiguration
            .withDefaults(ConfigFactory.parseMap(interceptorsConfig, "test-interceptors-configuration"))
            .getConfig("composer.session");
    }

}
