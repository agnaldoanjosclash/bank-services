package com.home.account.repository;

import com.home.account.data.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ClientRepository extends JpaRepository<Client, Long> {

    @Query("SELECT c FROM Client c WHERE c.document = ?1 AND c.version.active = 'A'")
    Client findByDocument(String document);
}
