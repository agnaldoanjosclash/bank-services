package com.home.account.data.model;

import com.home.account.data.enums.OperationType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(columnDefinition = "VARCHAR(255)", nullable = false)
    private String transactionId;

    @Column(columnDefinition = "TIMESTAMP", nullable = false)
    private LocalDateTime transactionDateTime;

    @Column(columnDefinition = "VARCHAR(512)", nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "VARCHAR(8)", nullable = false)
    private OperationType operationType;

    @Column(columnDefinition = "CHAR(1)", nullable = false)
    private String success;
}
