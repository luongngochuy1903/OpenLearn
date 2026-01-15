package com.example.online.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOG =
            LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private String getOrigin(RuntimeException ex) {
        StackTraceElement element = ex.getStackTrace()[0];
        return element.getClassName() + "#" + element.getMethodName();
    }

    // ================= BUSINESS EXCEPTION =================
    @ExceptionHandler({
            ResourceNotFoundException.class,
            BadRequestException.class,
            UnauthorizedException.class,
            ForbiddenException.class,
            AccessDeniedException.class
    })
    public ResponseEntity<Map<String, Object>> handleBusinessException(
            RuntimeException ex, WebRequest request) {

        HttpStatus status = resolveStatus(ex);
        String origin = getOrigin(ex);

        LOG.warn(
                "Business exception: status={}, path={}, origin={}, message={}",
                status.value(),
                request.getDescription(false),
                ex,
                ex.getMessage()
        );

        return buildResponse(status, ex.getMessage(), request);
    }

    // ================= SYSTEM EXCEPTION =================
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleSystemException(
            Exception ex, WebRequest request) {

        LOG.error(
                "System exception at path={}",
                request.getDescription(false),
                ex
        );

        return buildResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Internal server error",
                request
        );
    }

    // ================= HELPER =================
    private HttpStatus resolveStatus(RuntimeException ex) {
        if (ex instanceof ResourceNotFoundException) {
            return HttpStatus.NOT_FOUND;
        }
        if (ex instanceof BadRequestException) {
            return HttpStatus.BAD_REQUEST;
        }
        if (ex instanceof UnauthorizedException) {
            return HttpStatus.UNAUTHORIZED;
        }
        if (ex instanceof ForbiddenException) {
            return HttpStatus.FORBIDDEN;
        }
        if (ex instanceof AccessDeniedException){
            return HttpStatus.FORBIDDEN;
        }
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }

    private ResponseEntity<Map<String, Object>> buildResponse(
            HttpStatus status, String message, WebRequest request) {

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        body.put("path", request.getDescription(false).replace("uri=", ""));

        return new ResponseEntity<>(body, status);
    }
}
