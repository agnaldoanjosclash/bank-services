package com.home.account.repository;

import com.home.account.data.model.Operation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OperationRepository extends JpaRepository<Operation, String> {

}
