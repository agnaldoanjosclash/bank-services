package com.home.account.integration;

import com.home.account.data.dto.AccountResponseDTO;
import com.home.account.data.dto.MessageResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(url = "${client.feign.bank-kernel.url}", name = "kernel-bank")
public interface AccountKernelClient {

    @GetMapping("/kernel/account/{number}/agency/{agency}/clientDocument/{clientDocument}/typeDocument/{typeDocument}/balance")
    ResponseEntity<Double> balance(
            @PathVariable("number") final String number,
            @PathVariable("agency") final String agency,
            @PathVariable("clientDocument") final String clientDocument,
            @PathVariable("typeDocument") final String typeDocument
    );

    @GetMapping("/kernel/account/{number}/agency/{agency}/clientDocument/{clientDocument}/typeDocument/{typeDocument}/details")
    ResponseEntity<AccountResponseDTO> details(
            @PathVariable("number") final String number,
            @PathVariable("agency") final String agency,
            @PathVariable("clientDocument") final String clientDocument,
            @PathVariable("typeDocument") final String typeDocument
    );

    @GetMapping("/kernel/account/transaction/{transactionId}/details")
    ResponseEntity<MessageResponseDTO> transaction(@PathVariable("transactionId") String transactionId);
}
