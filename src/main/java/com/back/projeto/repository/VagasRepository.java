package com.back.projeto.repository;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.back.projeto.entity.Vagas;

import jakarta.transaction.Transactional;

public interface VagasRepository extends JpaRepository <Vagas, Long> {
    //  // Método para excluir vagas com data de criação anterior à data limite
    // void deleteByCreate_atBefore(LocalDateTime dataLimite);

    @Modifying
    @Transactional
    @Query("DELETE FROM Vagas v WHERE v.create_at < :dateTime")
    void deleteByCreate_atBefore(@Param("dateTime") LocalDateTime dateTime);
}
