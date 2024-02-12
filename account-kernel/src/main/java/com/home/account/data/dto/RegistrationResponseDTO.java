package com.home.account.data.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegistrationResponseDTO implements Serializable {

    private String document;

    private String name;

    private String documentType;

    private String failMessage;

    @Builder.Default
    private Boolean failService = false;
}
