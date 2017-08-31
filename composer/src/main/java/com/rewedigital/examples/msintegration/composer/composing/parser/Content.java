package com.rewedigital.examples.msintegration.composer.composing.parser;

import java.util.LinkedList;
import java.util.List;

public class Content {

    private String body = "";
    private List<String> assetLinks = new LinkedList<>();

    public String body() {
        return body;
    }

    public void body(String body) {
        this.body = body;
    }

    public List<String> assetLinks() {
        return assetLinks;
    }

    public void addAssetLink(String link) {
        assetLinks.add(link);
    }

}
