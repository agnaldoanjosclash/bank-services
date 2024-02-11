package com.home.account.service;

import com.home.account.data.dto.AccountResponseDTO;
import com.home.account.data.enums.DocumentType;

public interface RegisterDataService {
    AccountResponseDTO findAccountDetails(String number, String agency, String clientDocument, DocumentType documentType);

    Double consultBalance(String number, String agency, String clientDocument, DocumentType documentType);
}
