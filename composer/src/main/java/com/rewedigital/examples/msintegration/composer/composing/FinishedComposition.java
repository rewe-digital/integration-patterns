package com.rewedigital.examples.msintegration.composer.composing;

import java.util.LinkedList;
import java.util.List;

class FinishedComposition {

    private final String result;
    private final List<String> assetLinks;

    public FinishedComposition(String result, List<String> assetLinks) {
        this.result = result;
        this.assetLinks = new LinkedList<>(assetLinks);
    }

    public String composition() {
        return result;
    }

    public List<String> assetLinks() {
        return assetLinks;
    }

}
