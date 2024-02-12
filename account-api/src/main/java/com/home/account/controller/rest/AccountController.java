package com.home.account.controller.rest;

import com.home.account.data.dto.AccountResponseDTO;
import com.home.account.data.dto.MessageResponseDTO;
import com.home.account.data.dto.TransferRequestDTO;
import com.home.account.data.enums.DocumentType;
import com.home.account.service.CashOperationsService;
import com.home.account.service.RegisterDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/account")
public class AccountController {

    private final CashOperationsService cashOperationsService;

    private final RegisterDataService registerDataService;

    @GetMapping("/{number}/agency/{agency}/clientDocument/{clientDocument}/typeDocument/{typeDocument}/details")
    public ResponseEntity<AccountResponseDTO> details(
            @PathVariable("number") final String number,
            @PathVariable("agency") final String agency,
            @PathVariable("clientDocument") final String clientDocument,
            @PathVariable("typeDocument") final String tipoDocumento
    ) {

        var accountDetails = registerDataService.findAccountDetails(
                number,
                agency,
                clientDocument,
                DocumentType.valueOf(tipoDocumento.toUpperCase())
        );

        return accountDetails != null ? ResponseEntity.ok(accountDetails) :
                ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @GetMapping("/{number}/agency/{agency}/clientDocument/{clientDocument}/typeDocument/{typeDocument}/balance")
    public ResponseEntity<Double> balance(
            @PathVariable("number") final String number,
            @PathVariable("agency") final String agency,
            @PathVariable("clientDocument") final String clientDocument,
            @PathVariable("typeDocument") final String tipoDocumento
    ) {

        var balance = registerDataService.consultBalance(
                number,
                agency,
                clientDocument,
                DocumentType.valueOf(tipoDocumento.toUpperCase())
        );

        return balance != null ? ResponseEntity.ok(balance) : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @PostMapping("/transfer")
    public ResponseEntity<MessageResponseDTO> transfer(@Validated @RequestBody final TransferRequestDTO transferRequestDTO) {

        var messageDTO = cashOperationsService.transfer(transferRequestDTO);

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(messageDTO);
    }


}
