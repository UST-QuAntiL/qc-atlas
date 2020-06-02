package org.planqk.atlas.core.model.exceptions;

public class SqlConsistencyException extends RuntimeException {

	private static final long serialVersionUID = 1697519892942333680L;

	public SqlConsistencyException() {
	}

	public SqlConsistencyException(String message) {
		super(message);
	}

	public SqlConsistencyException(Throwable cause) {
		super(cause);
	}

	public SqlConsistencyException(String message, Throwable cause) {
		super(message, cause);
	}
	
}
