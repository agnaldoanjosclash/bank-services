package com.home.account.repository;

import com.home.account.data.model.Version;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VersionRepository extends JpaRepository<Version, String> {

    List<Version> findAllByActive(String active);
}
