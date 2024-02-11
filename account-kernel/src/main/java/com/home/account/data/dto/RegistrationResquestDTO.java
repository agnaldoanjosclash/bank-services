package com.home.account.data.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Transient;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegistrationResquestDTO implements Serializable {

    @JsonProperty("document")
    private String document;

    @JsonProperty("name")
    private String name;

    @JsonProperty("document_type")
    private String documentType;

    @Transient
    private String failMessage;

    @Transient
    @Builder.Default
    private Boolean failService = false;
}
