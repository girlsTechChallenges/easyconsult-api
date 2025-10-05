package com.fiap.easyconsult.infra.persistence.repository;

import com.fiap.easyconsult.infra.persistence.entity.ConsultEntity;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConsultRepository extends JpaRepository<ConsultEntity, Long> {

    @EntityGraph(attributePaths = {"patient", "professional"})
    @Query("SELECT c FROM ConsultEntity c")
    List<ConsultEntity> findAllWithDetails();


    @Query("SELECT c FROM ConsultEntity c WHERE c.patient.email = :email")
    List<ConsultEntity> findAllByPatientEmail(@Param("email") String email);

}