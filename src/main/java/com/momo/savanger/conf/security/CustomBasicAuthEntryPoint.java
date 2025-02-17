package com.momo.savanger.conf.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.momo.savanger.error.ApiErrorCode;
import com.momo.savanger.error.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

@RequiredArgsConstructor
public class CustomBasicAuthEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.addHeader("Content-Type", "application/json");

//        response.addHeader("Access-Control-Allow-Headers", "session");
//        response.addHeader("Access-Control-Allow-Methods", "HEAD,GET,PUT,POST,DELETE,PATCH");

        //This combined with spring security: cors disabled throws CORS error because the * is duplicated (*, *)
//        response.addHeader("Access-Control-Allow-Origin", "*");

        response.getWriter().println(this.objectMapper.writeValueAsString(
                new ErrorResponse(
                        ApiErrorCode.ERR_0003,
                        request.getRequestURL().toString(),
                        null
                )
        ));
    }
}
