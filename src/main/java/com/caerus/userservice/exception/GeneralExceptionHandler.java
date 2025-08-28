package com.caerus.userservice.exception;
import java.nio.file.AccessDeniedException;
import java.util.HashMap;
import java.util.Map;

import com.caerus.userservice.payload.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;



@RestControllerAdvice
@Slf4j
public class GeneralExceptionHandler extends ResponseEntityExceptionHandler {

	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
	            @NonNull HttpHeaders headers,
	            @NonNull HttpStatus status,
	            @NonNull WebRequest request) {
	Map<String, String> errors = new HashMap<>();
	ex.getBindingResult().getAllErrors()
	.forEach(x -> errors.put(((FieldError) x).getField(), x.getDefaultMessage()));
	return ResponseEntity.badRequest().body(errors);
	}


    private static final String CORRELATION_ID = "correlationId";

    @ExceptionHandler(GenericErrorResponse.class)
    public ResponseEntity<?> genericError(GenericErrorResponse exception, HttpServletRequest request) {
        log.error("Unexpected error", exception);
        return buildErrorResponse(exception, HttpStatus.INTERNAL_SERVER_ERROR, request);

    }

    @ExceptionHandler(Exception.class)
    public final ResponseEntity<ErrorResponse> handleAllException(Exception exception, HttpServletRequest request) {
        log.error("BadRequestException: {}", exception.getMessage());
        return buildErrorResponse(exception, HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> notFoundException(NotFoundException exception, HttpServletRequest request) {
        log.error("NotFoundException: {}", exception.getMessage());
        return buildErrorResponse(exception, HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> unauthorizedException(UnauthorizedException exception, HttpServletRequest request) {
        log.error("UnauthorizedException: {}", exception.getMessage());
        return buildErrorResponse(exception, HttpStatus.UNAUTHORIZED, request);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> accessDeniedException(AccessDeniedException exception, HttpServletRequest request) {
        log.error("AccessDeniedException: {}", exception.getMessage());
        return buildErrorResponse(exception, HttpStatus.FORBIDDEN, request);
    }

    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> resourceAlreadyExistsException(ResourceAlreadyExistsException exception, HttpServletRequest request) {
        log.error("ResourceAlreadyExistsException: {}", exception.getMessage());
        return buildErrorResponse(exception, HttpStatus.CONFLICT, request);
    }

    private ResponseEntity<ErrorResponse> buildErrorResponse(Exception ex, HttpStatus status, HttpServletRequest request) {
        String correlationId = MDC.get(CORRELATION_ID);
        ErrorResponse errorResponse = new ErrorResponse(
                status.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI(),
                correlationId
        );
        return new ResponseEntity<>(errorResponse, status);
    }
}