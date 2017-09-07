package com.rewedigital.examples.msintegration.composer.composing.parser;

import java.util.concurrent.CompletionStage;

public interface Composer {
    CompletionStage<Composition> compose(String template);
}