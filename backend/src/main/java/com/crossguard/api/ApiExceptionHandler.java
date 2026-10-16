package com.crossguard.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import java.time.Instant;
import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String,Object>> status(ResponseStatusException ex){return ResponseEntity.status(ex.getStatusCode()).body(Map.of("timestamp",Instant.now().toString(),"status",ex.getStatusCode().value(),"error",ex.getReason()==null?"Request failed":ex.getReason()));}
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String,Object>> invalid(MethodArgumentNotValidException ex){String message=ex.getBindingResult().getFieldErrors().stream().findFirst().map(e->e.getField()+": invalid value").orElse("Invalid request.");return ResponseEntity.badRequest().body(Map.of("timestamp",Instant.now().toString(),"status",400,"error",message));}
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String,Object>> unexpected(Exception ex){return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("timestamp",Instant.now().toString(),"status",500,"error","Unexpected server error."));}
}
