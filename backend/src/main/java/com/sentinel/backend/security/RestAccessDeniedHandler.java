package com.sentinel.backend.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sentinel.backend.common.ApiError;
import jakarta.servlet.http.*;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import java.time.Instant;

@Component
public class RestAccessDeniedHandler implements AccessDeniedHandler {
    public RestAccessDeniedHandler(ObjectMapper mapper){this.mapper=mapper;}
 private final ObjectMapper mapper;
 @Override public void handle(HttpServletRequest req,HttpServletResponse res,AccessDeniedException ex)throws java.io.IOException{
  res.setStatus(403);res.setContentType(MediaType.APPLICATION_JSON_VALUE);
  mapper.writeValue(res.getOutputStream(),new ApiError(Instant.now(),403,"Forbidden","Insufficient permissions",req.getRequestURI()));
 }
}
