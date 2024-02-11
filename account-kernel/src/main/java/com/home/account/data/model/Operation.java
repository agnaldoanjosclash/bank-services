package com.home.account.data.model;

import com.home.account.data.enums.OperationType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"transactionId"})
public class Operation {

    @Id
    @Column(columnDefinition = "VARCHAR(255)", nullable = false)
    private String transactionId;

    @ManyToOne
    @JoinColumn(name = "source_account_id")
    private Account sourceAccount;

    @ManyToOne(optional = false)
    @JoinColumn(name = "target_account_id", nullable = false)
    private Account targetAccount;

    @Column(columnDefinition = "NUMERIC(10,3)", nullable = false)
    private Double value;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "VARCHAR(8)", nullable = false)
    private OperationType operationType;

    @Column(columnDefinition = "TIMESTAMP", nullable = false)
    private LocalDateTime operationDate;

}
