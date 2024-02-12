package com.home.account.integration;

import com.home.account.data.dto.RegistrationResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(url="${client.feign.registration.url}", name="persons")
public interface PersonClient {

    @GetMapping("/person/document/{document}/type/{type}")
    ResponseEntity<RegistrationResponseDTO> persons(@PathVariable final String document, @PathVariable final String type);
}
