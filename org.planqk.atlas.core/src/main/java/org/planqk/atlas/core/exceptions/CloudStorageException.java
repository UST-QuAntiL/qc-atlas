package org.planqk.atlas.core.exceptions;

/**
 * Exception that is thrown if exception was thrown in the cloud storage.
 */
public class CloudStorageException extends RuntimeException {

    public CloudStorageException(String message) {
        super(message);
    }
}
