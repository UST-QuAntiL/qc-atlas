package org.planqk.atlas.core.model.exceptions;

/**
 * This exception is thrown when database consistency is about to be violated.
 *
 */
public class ConsistencyException extends RuntimeException {

    private static final long serialVersionUID = 1697519892942333680L;

    public ConsistencyException() {
    }

    public ConsistencyException(String message) {
        super(message);
    }

    public ConsistencyException(Throwable cause) {
        super(cause);
    }

    public ConsistencyException(String message, Throwable cause) {
        super(message, cause);
    }

}
