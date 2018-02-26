package com.rewedigital.examples.msintegration.composer.routing;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.rewedigital.examples.msintegration.composer.configuration.DefaultConfiguration;
import com.spotify.apollo.route.Rule;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class RoutingConfigurationTest {

    @Test
    public void allConfigParametersAreCoveredByDefaultConfig() {
        final RoutingConfiguration configuration =
            RoutingConfiguration.fromConfig(DefaultConfiguration.defaultConfiguration().getConfig("composer.routing"));
        assertThat(configuration.localRules()).isEmpty();
    }

    @Test
    public void createsLocalRouteFromConfiguration() {
        final RoutingConfiguration configuration =
            RoutingConfiguration.fromConfig(configWithLocalRoutes(
                localRoute("/test/path/<arg>", "GET", "https://target.service/{arg}", RouteTypeName.PROXY)));
        final List<Rule<Match>> localRules = configuration.localRules();
        assertThat(localRules).anySatisfy(rule -> {
            assertThat(rule.getPath()).isEqualTo("/test/path/<arg>");
            assertThat(rule.getMethods()).contains("GET");
            assertThat(rule.getTarget()).isEqualTo(Match.of("https://target.service/{arg}", RouteTypeName.PROXY));
        });
    }

    private static Map<String, Object> localRoute(final String path, final String method, final String target,
        final RouteTypeName type) {
        final Map<String, Object> result = new HashMap<>();
        result.put("path", path);
        result.put("method", method);
        result.put("target", target);
        result.put("type", type.toString());
        return result;
    }

    @SafeVarargs
    private static Config configWithLocalRoutes(final Map<String, Object>... routes) {
        final List<Map<String, Object>> routesConfigList = Arrays.asList(routes);
        final Map<String, Object> routesConfig = new HashMap<>();
        routesConfig.put("composer.routing.local-routes", routesConfigList);
        return DefaultConfiguration
            .withDefaults(ConfigFactory.parseMap(routesConfig, "test-routes-configuration"))
            .getConfig("composer.routing");
    }
}
