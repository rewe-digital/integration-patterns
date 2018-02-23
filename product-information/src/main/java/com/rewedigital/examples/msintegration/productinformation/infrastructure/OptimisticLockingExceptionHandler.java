package com.rewedigital.examples.msintegration.productinformation.infrastructure;

import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class OptimisticLockingExceptionHandler {

    @ExceptionHandler(OptimisticLockingFailureException.class)
    public ResponseEntity<Object> handleConflict(final OptimisticLockingFailureException exception,
        final WebRequest request) {
        return new ResponseEntity<>(HttpStatus.CONFLICT);
    }
}
