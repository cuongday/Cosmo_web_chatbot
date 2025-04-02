package com.ndc.be.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.ndc.be.domain.Setting;

@Repository
public interface SettingRepository extends JpaRepository<Setting, Long>, JpaSpecificationExecutor<Setting> {
    Setting findByName(String name);
} 