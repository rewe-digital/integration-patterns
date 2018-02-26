package com.rewedigital.examples.msintegration.composer.configuration;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigValue;

public class DefaultConfiguration {

    private static final Map<String, Object> defaultConfiguration = initDefaultConfigurationMap();

    public static Config withDefaults(final Config config) {
        return config.withFallback(defaultConfiguration());
    }

    public static Config defaultConfiguration() {
        return ConfigFactory.parseMap(defaultConfiguration, "default-configuration");
    }

    private static Map<String, Object> initDefaultConfigurationMap() {
        final Map<String, Object> result = new HashMap<>();
        result.put("composer.html.include-tag", "rewe-digital-include");
        result.put("composer.html.content-tag", "rewe-digital-content");
        result.put("composer.html.asset-options-attribute", "data-rd-options");

        result.put("composer.session.enabled", Boolean.TRUE);
        result.put("composer.session.cookie", "rdsession");
        result.put("composer.session.signing-algorithm", "HS512");
        result.put("composer.session.signing-key", "123");
        result.put("composer.session.interceptors", Collections.<ConfigValue>emptyList());
        
        result.put("composer.routing.local-routes", Collections.<ConfigValue>emptyList());
        return result;
    }
}
