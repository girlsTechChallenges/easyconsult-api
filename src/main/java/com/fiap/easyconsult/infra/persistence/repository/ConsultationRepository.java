package com.fiap.easyconsult.infra.persistence.repository;

import com.fiap.easyconsult.infra.persistence.entity.ConsultationEntity;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConsultationRepository extends JpaRepository<ConsultationEntity, Long> {

    @EntityGraph(attributePaths = {"patient", "professional"})
    @Query("SELECT c FROM ConsultationEntity c")
    List<ConsultationEntity> findAllWithDetails();


    @Query("SELECT c FROM ConsultationEntity c WHERE c.patient.email = :email")
    List<ConsultationEntity> findAllByPatientEmail(@Param("email") String email);

}
