package com.home.account.data.model;

import com.home.account.data.dto.RegistrationResponseDTO;
import com.home.account.data.enums.DocumentType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "ClientCache")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(
        of = {"id"}
)
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(columnDefinition = "VARCHAR(14)", nullable = false)
    private String document;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "VARCHAR(8)", nullable = false)
    private DocumentType documentType;

    @Column(columnDefinition = "VARCHAR(128)", nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "version", referencedColumnName = "version")
    private Version version;

    public RegistrationResponseDTO toDTO() {
        return RegistrationResponseDTO.builder()
                .name(this.name)
                .document(this.document)
                .documentType(this.documentType.toString().toLowerCase())
                .build();
    }
}
