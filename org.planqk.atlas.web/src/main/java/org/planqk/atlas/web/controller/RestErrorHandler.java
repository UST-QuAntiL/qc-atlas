package org.planqk.atlas.web.controller;

import org.planqk.atlas.core.model.exceptions.NotFoundException;
import org.planqk.atlas.core.model.exceptions.SqlConsistencyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@EnableWebMvc
@ControllerAdvice
public class RestErrorHandler {

	private final static Logger LOG = LoggerFactory.getLogger(RestErrorHandler.class);

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<?> handleInvalidRequestBody(MethodArgumentNotValidException e) {
		LOG.warn("Handling MethodArgumentNotValidException");
		return new ResponseEntity<>(e.getBindingResult().getFieldErrors(), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<?> handleInvalidJson(HttpMessageNotReadableException e) {
		LOG.error(e.getMessage(), e);
		return new ResponseEntity<>("Jackson cannot deserialize request", HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(SqlConsistencyException.class)
	public ResponseEntity<?> handleSqlConsistencyException(SqlConsistencyException e) {
		return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(NotFoundException.class)
	public ResponseEntity<?> handleNotFoundException(NotFoundException e) {
		return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
	}

}
