package com.home.account.repository;

import com.home.account.data.enums.DocumentType;
import com.home.account.data.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AccountRepository extends JpaRepository<Account, Long> {

    @Query("SELECT a FROM Account a WHERE a.agency = :agency " +
            "AND a.accountNumber = :accountNumber " +
            "AND a.clientDocument = :clientDocument " +
            "AND a.documentType = :documentType")
    Account findByAgencyAndAccountNumberAndClientDocumentAndTipoDocumento(@Param("agency") String agency,
                                                                          @Param("accountNumber") String accountNumber,
                                                                          @Param("clientDocument") String clientDocument,
                                                                          @Param("documentType") DocumentType documentType);
}
