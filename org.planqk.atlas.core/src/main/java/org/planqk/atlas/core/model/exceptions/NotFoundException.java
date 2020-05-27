package org.planqk.atlas.core.model.exceptions;

public class NotFoundException extends Exception {

	private static final long serialVersionUID = 3459823850282830910L;

	public NotFoundException() {
	}

	public NotFoundException(String message) {
		super(message);
	}

	public NotFoundException(Throwable cause) {
		super(cause);
	}

	public NotFoundException(String message, Throwable cause) {
		super(message, cause);
	}
	
}
