package com.home.account.config;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;

@ControllerAdvice
@Slf4j
@RequiredArgsConstructor
public class ApiExceptionHandler {


    @ExceptionHandler({NoHandlerFoundException.class})
    public ResponseEntity<ApiError> noHandlerFoundException(HttpServletRequest request,
                                                            NoHandlerFoundException e) {
        log.error("Internal Server Error", e);

        var apiError = ApiError.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .message(
                        String.format("Could not find the %s URL", request.getRequestURI())
                )
                .error("route_not_found")
                .build();

        return ResponseEntity.status(apiError.getStatus()).body(apiError);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleUknoownException(Exception e) {
        log.error("Internal Server Error", e);

        var apiError = ApiError.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message("Internal Server Error")
                .error("internal_server_error")
                .build();

        return ResponseEntity.status(apiError.getStatus()).body(apiError);
    }
}
