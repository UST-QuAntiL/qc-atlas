package org.planqk.atlas.web.controller;

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
		LOG.warn("Jackson cannot identify class since required identifier field is missing");
		return new ResponseEntity<>("Request is missing important fields for Jackson deserialisation",
				HttpStatus.BAD_REQUEST);
	}

}
