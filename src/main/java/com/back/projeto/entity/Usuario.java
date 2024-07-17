package com.back.projeto.entity;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "usuario")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "cpf", nullable = false, unique=true)
    private String cpf;

    @Column(name = "ra_matricula", nullable = false, unique=true)
    private String ra_matricula;

    @Column(name = "nome")
    private String nome;

    @Column(name = "tipo_usuario", nullable = false)
    private String tipo_usuario;

    @Column(name = "email", nullable = false, unique=true)
    private String email;

    @Column(name = "senha")
    private String senha;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "update_at")
    private LocalDateTime update_at;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getRa_matricula() {
        return ra_matricula;
    }

    public void setRa_matricula(String ra_matricula) {
        this.ra_matricula = ra_matricula;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getTipo_usuario() {
        return tipo_usuario;
    }

    public void setTipo_usuario(String tipo_usuario) {
        this.tipo_usuario = tipo_usuario;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public LocalDateTime getUpdate_at() {
        return update_at;
    }

    public void setUpdate_at(LocalDateTime update_at) {
        this.update_at = update_at;
    }
}
