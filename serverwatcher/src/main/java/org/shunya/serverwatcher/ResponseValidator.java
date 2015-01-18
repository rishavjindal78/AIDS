package org.shunya.serverwatcher;

public interface ResponseValidator {
    boolean validate(ServerComponent serverComponent, ServerResponse serverResponse);
}
