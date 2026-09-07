package com.sentinel.backend.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sentinel.backend.common.ApiError;
import jakarta.servlet.http.*;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import java.time.Instant;

@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {
    public RestAuthenticationEntryPoint(ObjectMapper mapper){this.mapper=mapper;}
 private final ObjectMapper mapper;
 @Override public void commence(HttpServletRequest req,HttpServletResponse res,AuthenticationException ex)throws java.io.IOException{
  res.setStatus(401);res.setContentType(MediaType.APPLICATION_JSON_VALUE);
  mapper.writeValue(res.getOutputStream(),new ApiError(Instant.now(),401,"Unauthorized","Authentication required",req.getRequestURI()));
 }
}
