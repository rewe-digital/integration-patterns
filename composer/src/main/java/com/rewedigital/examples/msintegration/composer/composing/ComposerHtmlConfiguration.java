package com.rewedigital.examples.msintegration.composer.composing;

import com.typesafe.config.Config;

public class ComposerHtmlConfiguration {

    private final String includeTag;
    private final String contentTag;
    private final String assetOptionsAttribute;

    public static ComposerHtmlConfiguration fromConfig(final Config config) {
        return new ComposerHtmlConfiguration(config.getString("include-tag"), config.getString("content-tag"),
            config.getString("asset-options-attribute"));
    }

    ComposerHtmlConfiguration(final String includeTag, final String contentTag, final String assetOptionsAttribute) {
        this.includeTag = includeTag;
        this.contentTag = contentTag;
        this.assetOptionsAttribute = assetOptionsAttribute;
    }

    public String includeTag() {
        return includeTag;
    }

    public String contentTag() {
        return contentTag;
    }

    public String assetOptionsAttribute() {
        return assetOptionsAttribute;
    }
}
