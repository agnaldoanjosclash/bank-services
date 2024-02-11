package com.home.account.data.model;

import com.home.account.data.enums.AccountStatus;
import com.home.account.data.enums.DocumentType;
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
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(
        of = {"id"}
)
public class Account {

    public static final double INITIAL_TRANSFER_VALUE = 0.0;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(columnDefinition = "CHAR(4)", nullable = false)
    private String agency;

    @Column(columnDefinition = "VARCHAR(7)", nullable = false)
    private String accountNumber;

    @Column(columnDefinition = "VARCHAR(14)", nullable = false)
    private String clientDocument;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "VARCHAR(8)", nullable = false)
    private DocumentType documentType;

    @Column(columnDefinition = "NUMERIC(10,3)", nullable = false)
    private Double balance;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "VARCHAR(8)", nullable = false)
    private AccountStatus status;

    @Column(columnDefinition = "NUMERIC(10,3)")
    private Double lastTransferValue;

    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime lastDateTimeTransfer;

    public Account evaluateTransferInformation() {
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        if (this.getLastDateTimeTransfer() == null) {
            this.setLastTransferValue(INITIAL_TRANSFER_VALUE);
            this.setLastDateTimeTransfer(LocalDateTime.now());
        } else {
            if (this.getLastDateTimeTransfer().isBefore(startOfDay)) {
                this.setLastTransferValue(INITIAL_TRANSFER_VALUE);
                this.setLastDateTimeTransfer(LocalDateTime.now());
            }
        }

        return this;
    }

}
