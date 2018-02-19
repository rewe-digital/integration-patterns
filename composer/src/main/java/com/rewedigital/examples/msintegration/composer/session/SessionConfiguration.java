package com.rewedigital.examples.msintegration.composer.session;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import com.rewedigital.examples.msintegration.composer.session.SessionLifecycle.Interceptor;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigList;
import com.typesafe.config.ConfigValue;

public class SessionConfiguration {

    private final boolean sessionEnabled;
    private final String cookieName;
    private final String signingAlgorithm;
    private final List<SessionLifecycle.Interceptor> interceptors;

    public static SessionConfiguration fromConfig(final Config config) {
        return new SessionConfiguration(config.getBoolean("enabled"), config.getString("cookie"),
            config.getString("signing-algorithm"), buildInterceptors(config.getList("interceptors")));
    }

    private static List<SessionLifecycle.Interceptor> buildInterceptors(final ConfigList configList) {
        final List<SessionLifecycle.Interceptor> interceptors = new LinkedList<>();
        configList
            .forEach(c -> buildInterceptor(c)
                .ifPresent(i -> interceptors.add(i)));
        return interceptors;
    }

    private static Optional<Interceptor> buildInterceptor(final ConfigValue configValue) {
        
        @SuppressWarnings("unchecked")
        final Map<String, Object> data = (Map<String, Object>) configValue.unwrapped();
        final String type = (String) data.get("type");
        if(type == null) {
            throw new ConfigurationException("Required configuration property 'type' for session interceptor is missing");
        }
        switch (type) {
            case "SessionIdInterceptor":
                return Optional.of(new SessionIdInterceptor());
        }
        return Optional.empty();
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
