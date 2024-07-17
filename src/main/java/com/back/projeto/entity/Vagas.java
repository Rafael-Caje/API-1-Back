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
@Table(name = "vagas")
public class Vagas {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_vaga")
    private Long id;

    @Column(name = "local_dado")
    private String local_dado;

    @Column(name = "nome_vaga")
    private String nome_vaga;

    @Column(name = "localizacao")
    private String localizacao;

    @Column(name = "tipo_vaga")
    private String tipo_vaga;

    @Column(name = "area")
    private String area;

    @Column(name = "nivel_experiencia")
    private String nivel_experiencia;

    @Column(name = "descricao", columnDefinition = "TEXT")
    private String descricao;

    @Column(name = "link")
    private String link;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "create_at")
    private LocalDateTime create_at;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "update_at")
    private LocalDateTime update_at;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLocal_dado() {
        return local_dado;
    }

    public void setLocal_dado(String local_dado) {
        this.local_dado = local_dado;
    }

    public String getNome_vaga() {
        return nome_vaga;
    }

    public void setNome_vaga(String nome_vaga) {
        this.nome_vaga = nome_vaga;
    }

    public String getLocalizacao() {
        return localizacao;
    }

    public void setLocalizacao(String localizacao) {
        this.localizacao = localizacao;
    }

    public String getTipo_vaga() {
        return tipo_vaga;
    }

    public void setTipo_vaga(String tipo_vaga) {
        this.tipo_vaga = tipo_vaga;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getNivel_experiencia() {
        return nivel_experiencia;
    }

    public void setNivel_experiencia(String nivel_experiencia) {
        this.nivel_experiencia = nivel_experiencia;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public LocalDateTime getCreate_at() {
        return create_at;
    }

    public void setCreate_at(LocalDateTime create_at) {
        this.create_at = create_at;
    }

    public LocalDateTime getUpdate_at() {
        return update_at;
    }

    public void setUpdate_at(LocalDateTime update_at) {
        this.update_at = update_at;
    }

}
