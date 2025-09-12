package com.fiap.easyconsult.infra.persistence.repository;

import com.fiap.easyconsult.infra.persistence.entity.ConsultationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConsultationRepository extends JpaRepository<ConsultationEntity, Long> {
}
