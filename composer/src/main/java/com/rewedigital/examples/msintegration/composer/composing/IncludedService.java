package com.rewedigital.examples.msintegration.composer.composing;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;

import com.spotify.apollo.Client;
import com.spotify.apollo.Request;
import com.spotify.apollo.Response;

import okio.ByteString;

public class IncludedService {

    private CompletionStage<Response<ByteString>> futureServiceResponse = null;
    private final Map<String, String> attributes = new HashMap<>();
    private int startOffset;
    private int endOffset;

    public void put(final String attribute, final String value) {
        attributes.put(attribute, value);
    }

    public void startOffset(final int startOffset) {
        this.startOffset = startOffset;
    }

    public void endOffset(final int endOffset) {
        this.endOffset = endOffset;
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    public String path() {
        return attributes.get("path");
    }

    public int startOffset() {
        return startOffset;
    }

    public int endOffset() {
        return endOffset;
    }

    public void fetchContent(final Client client) {
        futureServiceResponse = client.send(Request.forUri(this.path(), "GET"));
    }

    private String content() {
        if (futureServiceResponse == null) {
            throw new IllegalStateException("Call fetchContent first.");
        }
        // FIXME: Status code, is there a response and parsing...
        try {
            return futureServiceResponse.toCompletableFuture().get().payload().get().utf8();
        } catch (InterruptedException | ExecutionException e) {
            return "fallback";
        }
    }

    public int inject(final StringWriter writer, final String baseTemplate, final int start) {
        writer.write(baseTemplate, start, startOffset() - start);
        writer.write(content());
        return endOffset();
    }

}
