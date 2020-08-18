package org.planqk.atlas.web.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class MissingIdentifierException extends RuntimeException {

    public MissingIdentifierException() {
    }

    public MissingIdentifierException(String message) {
        super(message);
    }

    public MissingIdentifierException(String message, Throwable cause) {
        super(message, cause);
    }

    public MissingIdentifierException(Throwable cause) {
        super(cause);
    }
}
