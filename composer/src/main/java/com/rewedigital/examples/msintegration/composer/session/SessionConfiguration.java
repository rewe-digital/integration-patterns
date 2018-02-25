package com.rewedigital.examples.msintegration.composer.session;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rewedigital.examples.msintegration.composer.session.SessionHandler.Interceptor;
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
    private final List<SessionHandler.Interceptor> interceptors;

    public static SessionConfiguration fromConfig(final Config config) {
        final boolean enabled = config.getBoolean("enabled");
        return new SessionConfiguration(enabled, config.getString("cookie"),
            config.getString("signing-algorithm"), buildInterceptors(config.getList("interceptors"), enabled));
    }

    private static List<SessionHandler.Interceptor> buildInterceptors(final ConfigList configList, final boolean enabled) {
        if (!enabled) {
            return Collections.emptyList();
        }

        return configList.stream()
            .map(SessionConfiguration::buildInterceptor)
            .collect(Collectors.toList());
    }

    private static Interceptor buildInterceptor(final ConfigValue configValue) {
        final Config config = configValue.atKey("interceptor").getConfig("interceptor");
        final String type = config.getString("type");

        try {
            final Config args = config.withFallback(ConfigFactory.empty().atKey("args")).getConfig("args");
            final Interceptor result = (Interceptor) Class.forName(type).getConstructor(Config.class).newInstance(args);
            LOGGER.info("registered Session Interceptor of type {}", type);
            return result;
        } catch (final Exception e) {
            throw new ConfigException.Generic("Unable to instantiate Session Intercetor of type [" + type + "]",
                e);
        }
    }

    SessionConfiguration(final boolean sessionEnabled, final String cookieName, final String signingAlgorithm,
        final List<SessionHandler.Interceptor> interceptors) {
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
