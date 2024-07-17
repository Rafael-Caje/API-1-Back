package com.back.projeto.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.back.projeto.entity.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    
}
