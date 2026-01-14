package com.BharatCrypto.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.transaction.TransactionSystemException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobelExeptions {
	
	
	@ExceptionHandler(UserException.class)
	public ResponseEntity<Map<String, String>> userExceptionHandler(UserException ue, WebRequest req) {
		Map<String, String> error = new HashMap<>();
		error.put("message", ue.getMessage());
		error.put("details", req.getDescription(false));
		return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
		Map<String, String> errors = new HashMap<>();
		ex.getBindingResult().getFieldErrors().forEach(error ->
				errors.put(error.getField(), error.getDefaultMessage()));
		return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<Map<String, String>> handleConstraintViolationException(ConstraintViolationException ex) {
		Map<String, String> errors = new HashMap<>();
		ex.getConstraintViolations().forEach(violation -> {
			String propertyPath = violation.getPropertyPath().toString();
			String field = propertyPath.substring(propertyPath.lastIndexOf('.') + 1);
			errors.put(field, violation.getMessage());
		});
		return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(TransactionSystemException.class)
	public ResponseEntity<Map<String, String>> handleTransactionSystemException(TransactionSystemException ex) {
		// Recursively find the root cause
		Throwable cause = ex.getCause();
		while (cause != null) {
			if (cause instanceof ConstraintViolationException) {
				return handleConstraintViolationException((ConstraintViolationException) cause);
			}
			cause = cause.getCause();
		}
		
		// If no ConstraintViolationException found, return generic error
		Map<String, String> errors = new HashMap<>();
		Throwable mostSpecificCause = ex.getMostSpecificCause();
		errors.put("message", mostSpecificCause != null ? mostSpecificCause.getMessage() : ex.getMessage());
		return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<Map<String, String>> handleDataIntegrityViolationException(DataIntegrityViolationException ex, WebRequest request) {
		Map<String, String> error = new HashMap<>();
		error.put("message", "Data Integrity Violation: " + ex.getMostSpecificCause().getMessage());
		error.put("details", request.getDescription(false));
		return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException ex, WebRequest request) {
		Map<String, String> error = new HashMap<>();
		error.put("message", ex.getMessage());
		error.put("details", request.getDescription(false));
		return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<Map<String, String>> handleOtherExceptions(Exception ex, WebRequest request) {
		System.out.println("Global Exception Handler caught: " + ex.getClass().getName() + " - " + ex.getMessage());
		Map<String, String> errors = new HashMap<>();
		errors.put("message", ex.getMessage());
		errors.put("details", request.getDescription(false));
		return new ResponseEntity<>(errors, HttpStatus.INTERNAL_SERVER_ERROR);
	}

}
