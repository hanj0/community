package com.han.community.repository;

import com.han.community.entity.sanction.Sanction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SanctionRepository extends JpaRepository<Sanction, Long> {


}
