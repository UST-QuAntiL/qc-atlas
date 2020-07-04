
package org.planqk.atlas.web.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "The value of the ComputingResourcePorperty is not valid for the given type.")
public class InvalidResourceTypeValueException extends RuntimeException {
    public InvalidResourceTypeValueException(String message) {
        super(message);
    }
}
