package com.back.projeto.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.back.projeto.entity.Vagas;

public interface VagasRepository extends JpaRepository <Vagas, Long> {
    
}
