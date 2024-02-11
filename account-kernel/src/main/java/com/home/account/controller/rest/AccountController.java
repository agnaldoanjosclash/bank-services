package com.home.account.controller.rest;

import com.home.account.data.dto.AccountResponseDTO;
import com.home.account.data.dto.MessageResponseDTO;
import com.home.account.data.dto.TransferRequestDTO;
import com.home.account.data.enums.DocumentType;
import com.home.account.data.enums.MessageStatus;
import com.home.account.service.CashOperationsService;
import com.home.account.service.RegisterDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/kernel/account")
public class AccountController {

    private final CashOperationsService cashOperationsService;

    private final RegisterDataService registerDataService;

    @GetMapping("/{number}/agency/{agency}/clientDocument/{clientDocument}/typeDocument/{typeDocument}/details")
    public ResponseEntity<AccountResponseDTO> details(
            @PathVariable("number") final String number,
            @PathVariable("agency") final String agency,
            @PathVariable("clientDocument") final String clientDocument,
            @PathVariable("typeDocument") final String typeDocument
    ) {

        var accountDetails = registerDataService.findAccountDetails(
                number,
                agency,
                clientDocument,
                DocumentType.valueOf(typeDocument.toUpperCase())
        );

        return accountDetails != null ? ResponseEntity.status(HttpStatus.OK).body(accountDetails) :
                ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @GetMapping("/{number}/agency/{agency}/clientDocument/{clientDocument}/typeDocument/{typeDocument}/balance")
    public ResponseEntity<Double> balance(
            @PathVariable("number") final String number,
            @PathVariable("agency") final String agency,
            @PathVariable("clientDocument") final String clientDocument,
            @PathVariable("typeDocument") final String typeDocument
    ) {

        var balance = registerDataService.consultBalance(
                number,
                agency,
                clientDocument,
                DocumentType.valueOf(typeDocument.toUpperCase())
        );

        return balance != null ? ResponseEntity.status(HttpStatus.OK).body(balance) :
                ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @PostMapping("/transfer")
    public ResponseEntity<MessageResponseDTO> transfer(@RequestBody final TransferRequestDTO transferRequestDTO) {

        var messageDTO = cashOperationsService.transfer(transferRequestDTO);

        return messageDTO.getStatus() == MessageStatus.SUCCESS ?
                ResponseEntity.status(HttpStatus.ACCEPTED).body(messageDTO):
                ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).body(messageDTO);
    }

    @GetMapping("/transaction/{transactionId}/details")
    public ResponseEntity<MessageResponseDTO> transaction(@PathVariable("transactionId") String transactionId) {

        var messageDTO = cashOperationsService.transactionQuery(transactionId);

        return messageDTO != null ?
                ResponseEntity.status(HttpStatus.OK).body(messageDTO):
                ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

}
