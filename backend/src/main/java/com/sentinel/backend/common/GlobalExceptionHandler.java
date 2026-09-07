package com.sentinel.backend.common;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.*;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import java.util.NoSuchElementException;
import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(IllegalArgumentException.class) ResponseEntity<ApiError> badRequest(IllegalArgumentException e,HttpServletRequest r){return build(HttpStatus.BAD_REQUEST,e.getMessage(),r);}
    @ExceptionHandler(MethodArgumentNotValidException.class) ResponseEntity<ApiError> validation(MethodArgumentNotValidException e,HttpServletRequest r){return build(HttpStatus.BAD_REQUEST,"Invalid request data",r);}
    @ExceptionHandler(BadCredentialsException.class) ResponseEntity<ApiError> credentials(BadCredentialsException e,HttpServletRequest r){return build(HttpStatus.UNAUTHORIZED,"Invalid email or password",r);}
    @ExceptionHandler(NoSuchElementException.class) ResponseEntity<ApiError> notFound(NoSuchElementException e,HttpServletRequest r){return build(HttpStatus.NOT_FOUND,e.getMessage(),r);}
    @ExceptionHandler(SecurityException.class) ResponseEntity<ApiError> security(SecurityException e,HttpServletRequest r){return build(HttpStatus.UNAUTHORIZED,"Authentication or token validation failed",r);}
    @ExceptionHandler(IllegalStateException.class) ResponseEntity<ApiError> state(IllegalStateException e,HttpServletRequest r){return build(HttpStatus.CONFLICT,e.getMessage(),r);}
    @ExceptionHandler(Exception.class) ResponseEntity<ApiError> unexpected(Exception e,HttpServletRequest r){return build(HttpStatus.INTERNAL_SERVER_ERROR,"An unexpected server error occurred",r);}
    private ResponseEntity<ApiError> build(HttpStatus s,String m,HttpServletRequest r){return ResponseEntity.status(s).body(new ApiError(Instant.now(),s.value(),s.getReasonPhrase(),m,r.getRequestURI()));}
}
