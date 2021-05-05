/*******************************************************************************
 * Copyright (c) 2020 the qc-atlas contributors.
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/

package org.planqk.atlas.web.controller;

import java.beans.PropertyChangeEvent;
import java.util.NoSuchElementException;

import org.planqk.atlas.core.exceptions.CloudStorageException;
import org.planqk.atlas.core.exceptions.EntityReferenceConstraintViolationException;
import org.planqk.atlas.core.exceptions.InvalidResourceTypeValueException;
import org.planqk.atlas.web.controller.exceptions.InvalidRequestException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import lombok.extern.slf4j.Slf4j;

@ControllerAdvice
@Slf4j
public class AtlasExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BindingResult> handleInvalidRequestBody(MethodArgumentNotValidException e) {
        log.warn(e.getMessage(), e);
        return ResponseEntity.badRequest().body(e.getBindingResult());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<RuntimeException> handleInvalidJson(HttpMessageNotReadableException e) {
        log.error(e.getMessage(), e);
        return ResponseEntity.badRequest().body(new RuntimeException(e.getMessage()));
    }

    @ExceptionHandler(CloudStorageException.class)
    public ResponseEntity<CloudStorageException> handleCloudStorageException(
            CloudStorageException e) {
        log.error(e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e);
    }

    @ExceptionHandler(EntityReferenceConstraintViolationException.class)
    public ResponseEntity<EntityReferenceConstraintViolationException> handleEntityReferenceConstraintViolationException(
            EntityReferenceConstraintViolationException e) {
        log.warn(e.getMessage(), e);
        return ResponseEntity.badRequest().body(e);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<DataIntegrityViolationException> handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        log.warn(e.getMessage(), e);
        return ResponseEntity.badRequest().body(e);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<IllegalArgumentException> handleIllegalArgumentException(IllegalArgumentException e) {
        log.warn(e.getMessage(), e);
        return ResponseEntity.badRequest().body(e);
    }

    @ExceptionHandler(InvalidResourceTypeValueException.class)
    public ResponseEntity<InvalidResourceTypeValueException> handleInvalidResourceTypeValueException(
            InvalidResourceTypeValueException e) {
        log.warn(e.getMessage(), e);
        return ResponseEntity.badRequest().body(e);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<PropertyChangeEvent> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        log.warn(e.getMessage(), e);
        return ResponseEntity.badRequest().body(e.getPropertyChangeEvent());
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<NoSuchElementException> handleNoSuchElementException(NoSuchElementException e) {
        log.warn(e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e);
    }

    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<InvalidRequestException> handleInvalidRequestException(InvalidRequestException e) {
        log.warn(e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e);
    }

    @ExceptionHandler(EmptyResultDataAccessException.class)
    public ResponseEntity<EmptyResultDataAccessException> handleEmptyResultDataAccessException(EmptyResultDataAccessException e) {
        log.warn(e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e);
    }

    // Return stacktrace to client if another exception occurs.
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Exception> handleOtherException(Exception e) {
        log.error(e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e);
    }
}
