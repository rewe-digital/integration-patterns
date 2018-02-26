package com.rewedigital.examples.msintegration.composer.session;

import java.util.HashMap;
import java.util.Map;

public class Sessions {

    public static SessionRoot cleanSessionRoot(final String key, final String value) {
        return sessionRoot(key, value, false);
    }

    public static SessionRoot dirtySessionRoot(final String key, final String value) {
        return sessionRoot(key, value, true);
    }

    public static SessionRoot sessionRoot(final String key, final String value) {
        final HashMap<String, String> data = new HashMap<>();
        data.put(key, value);
        return SessionRoot.of(data);
    }

    public static SessionRoot sessionRoot(final String key, final String value, final boolean dirty) {
        final HashMap<String, String> data = new HashMap<>();
        data.put(key, value);
        return SessionRoot.of(data, dirty);
    }

    public static SessionRoot sessionRootExpiringAt(final long epochSeconds, final String key, final String value) {
        final Map<String, String> data = new HashMap<>();
        data.put("x-rd-session-id", "1234");
        data.put("expires-at", Long.toString(epochSeconds));
        data.put(key, value);
        return SessionRoot.of(data);
    }
}
