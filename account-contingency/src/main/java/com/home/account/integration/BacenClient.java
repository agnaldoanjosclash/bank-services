package com.home.account.integration;

import com.home.account.data.dto.RegistrationResquestDTO;
import com.home.account.data.dto.TransferRequestDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(url="${client.feign.bacen.url}", name="bacen")
public interface BacenClient {

    @PostMapping("/bacen/registro/transferencias")
    ResponseEntity transferRegistration(@RequestBody final TransferRequestDTO transferResponseDTO);
}
