package com.home.account.config;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ApiError {

    private int status;
    private String message;
    private String error;
}
