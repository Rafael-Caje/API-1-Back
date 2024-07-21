package com.back.projeto.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.back.projeto.entity.Vagas;

import jakarta.transaction.Transactional;

public interface VagasRepository extends JpaRepository <Vagas, Long> {
    @Modifying
    @Transactional
    @Query("DELETE FROM Vagas v WHERE v.create_at < :dateTime")
    void deleteByCreate_atBefore(@Param("dateTime") LocalDateTime dateTime);

    @Query("SELECT v FROM Vagas v ORDER BY v.create_at DESC")
    List<Vagas> findAllOrderByCreate_atDesc();

    List<Vagas> findByUsuarioId(Long usuarioId);

    @Query("SELECT v FROM Vagas v WHERE v.usuario.tipo_usuario = :tipoUsuario")
    List<Vagas> findByUsuarioTipoUsuario(@Param("tipoUsuario") String tipoUsuario);

    @Query("SELECT v FROM Vagas v WHERE LOWER(v.nome_vaga) LIKE LOWER(CONCAT('%', :nomeVaga, '%')) ORDER BY v.nome_vaga ASC")
    List<Vagas> findByNomeVagaContainingIgnoreCaseOrderByNomeVagaAsc(@Param("nomeVaga") String nomeVaga);
}
