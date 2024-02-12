package com.home.account.data.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "VersionCache")
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Version {

    @Id
    private String version;

    @Column(columnDefinition = "CHAR(1)", nullable = false)
    private String active;

}
