package com.rewedigital.examples.msintegration.composer.session;

import com.typesafe.config.Config;

public class SessionConfiguration {

    private final boolean sessionEnabled;
    private final String cookieName;
    private final String signingAlgorithm;

    public static SessionConfiguration fromConfig(final Config config) {
        return new SessionConfiguration(config.getBoolean("enabled"), config.getString("cookie"),
            config.getString("signing-algorithm"));
    }

    SessionConfiguration(final boolean sessionEnabled, final String cookieName, final String signingAlgorithm) {
        this.sessionEnabled = sessionEnabled;
        this.cookieName = cookieName;
        this.signingAlgorithm = signingAlgorithm;
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

}
