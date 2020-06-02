package org.planqk.atlas.core.model.exceptions;

public class NoContentException extends RuntimeException {

	private static final long serialVersionUID = 2729077142482170382L;

	public NoContentException() {
	}

	public NoContentException(String message) {
		super(message);
	}

	public NoContentException(Throwable cause) {
		super(cause);
	}

	public NoContentException(String message, Throwable cause) {
		super(message, cause);
	}
}
