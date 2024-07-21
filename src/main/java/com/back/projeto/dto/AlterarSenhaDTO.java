package com.back.projeto.dto;

public class AlterarSenhaDTO {
    private String ra_matricula;
    private String senhaAntiga;
    private String novaSenha;

    // Getters e Setters

    public String getRa_matricula() {
        return ra_matricula;
    }

    public void setRa_matricula(String ra_matricula) {
        this.ra_matricula = ra_matricula;
    }

    public String getSenhaAntiga() {
        return senhaAntiga;
    }

    public void setSenhaAntiga(String senhaAntiga) {
        this.senhaAntiga = senhaAntiga;
    }

    public String getNovaSenha() {
        return novaSenha;
    }

    public void setNovaSenha(String novaSenha) {
        this.novaSenha = novaSenha;
    }
}
