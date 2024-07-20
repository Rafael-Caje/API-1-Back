package com.back.projeto.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.back.projeto.entity.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> getByEmail(String email);

    Optional<Usuario> findByEmail(String email);

    @Query("SELECT u FROM Usuario u WHERE u.ra_matricula = :ra_matricula AND u.cpf = :cpf")
    Optional<Usuario> findByRa_matriculaAndCpf(@Param("ra_matricula") String ra_matricula, @Param("cpf") String cpf);

    @Query("SELECT u FROM Usuario u WHERE u.ra_matricula = :ra_matricula")
    Optional<Usuario> findByRa_matricula(@Param("ra_matricula") String ra_matricula);
    
    @Query("SELECT u FROM Usuario u WHERE u.nome LIKE %:nome%")
    List<Usuario> findByNomeContaining(@Param("nome") String nome);

}
