package com.rewedigital.examples.msintegration.composer.session;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rewedigital.examples.msintegration.composer.session.SessionLifecycle.Interceptor;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigException;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigList;
import com.typesafe.config.ConfigValue;

public class SessionConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(SessionConfiguration.class);

    private final boolean sessionEnabled;
    private final String cookieName;
    private final String signingAlgorithm;
    private final List<SessionLifecycle.Interceptor> interceptors;

    public static SessionConfiguration fromConfig(final Config config) {
        return new SessionConfiguration(config.getBoolean("enabled"), config.getString("cookie"),
            config.getString("signing-algorithm"), buildInterceptors(config.getList("interceptors")));
    }

    private static List<SessionLifecycle.Interceptor> buildInterceptors(final ConfigList configList) {
        return configList.stream()
            .flatMap(SessionConfiguration::buildInterceptor)
            .collect(Collectors.toList());
    }

    private static Stream<Interceptor> buildInterceptor(final ConfigValue configValue) {
        final Config config = configValue.atKey("interceptor").getConfig("interceptor");
        final String type = config.getString("type");

        try {
            final Config args = config.withFallback(ConfigFactory.empty().atKey("args"));
            final Interceptor result = (Interceptor) Class.forName(type).getConstructor(Config.class).newInstance(args);
            LOGGER.info("registered Configuration Interceptor of type {}", type);
            return Stream.of(result);
        } catch (final Exception e) {
            throw new ConfigException.Generic("Unable to instantiate Configuration Intercetor of type [" + type + "]",
                e);
        }
    }

    SessionConfiguration(final boolean sessionEnabled, final String cookieName, final String signingAlgorithm,
        final List<SessionLifecycle.Interceptor> interceptors) {
        this.sessionEnabled = sessionEnabled;
        this.interceptors = Objects.requireNonNull(interceptors);
        this.cookieName = Objects.requireNonNull(cookieName);
        this.signingAlgorithm = Objects.requireNonNull(signingAlgorithm);
    }

    public String cookieName() {
        return cookieName;
    }

    public String signingAlgorithm() {
        return signingAlgorithm;
    }

    public boolean sessionEnabled() {
        return sessionEnabled;
    }

    public List<Interceptor> interceptors() {
        return interceptors;
    }

}
