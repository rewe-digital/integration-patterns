package com.rewedigital.examples.msintegration.composer.configuration;

import java.util.HashMap;
import java.util.Map;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class DefaultConfiguration {

    public static Config withDefaults(final Config config) {
        return config.withFallback(defaultConfiguration());
    }

    public static Config defaultConfiguration() {
        return ConfigFactory.parseMap(defaultConfigurationMap(), "default-configuration");
    }

    private static Map<String, Object> defaultConfigurationMap() {
        Map<String, Object> result = new HashMap<>();
        result.put("composer.html.include-tag", "rewe-digital-include");
        result.put("composer.html.content-tag", "rewe-digital-content");
        result.put("composer.html.asset-options-attribute", "data-rd-options");
        return result;
    }

}
