package com.example.online.authentication.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Component
public class OAuth2AuthenticationFailureHandler implements AuthenticationFailureHandler {

    private static final String FRONTEND_REDIRECT = "http://localhost:3000/oauth2/success";

    @Override
    public void onAuthenticationFailure(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException exception
    ) throws IOException {

        String errorCode = "oauth2_error";
        String message = "Google authorization fail";

        if (exception instanceof OAuth2AuthenticationException oae) {
            errorCode = oae.getError().getErrorCode();
            message = oae.getMessage();
        } else if (exception.getMessage() != null) {
            message = exception.getMessage();
        }

        String redirectUrl = UriComponentsBuilder
                .fromUriString(FRONTEND_REDIRECT)
                .queryParam("success", "false")
                .queryParam("error", errorCode)
                .queryParam("message", message)
                .build()
                .toUriString();

        response.sendRedirect(redirectUrl);
    }
}
