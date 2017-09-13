package com.rewedigital.examples.msintegration.composer.composing;

import org.attoparser.IMarkupHandler;

public interface CompositionHandler {
    
    IMarkupHandler markupHandler(final IMarkupHandler next);

}
