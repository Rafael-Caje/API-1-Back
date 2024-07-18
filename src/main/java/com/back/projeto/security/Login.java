package com.back.projeto.security;

public class Login {

    private String ra_matricula;
    private String email;
    public String getRa_matricula() {
        return ra_matricula;
    }

    public void setRa_matricula(String ra_matricula) {
        this.ra_matricula = ra_matricula;
    }

    private String senha;
    private String autorizacoes;
    private String token;

    public Login() {
        // Construtor padrão
    }

    // Getters e Setters

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

    public String getAutorizacoes() {
        return autorizacoes;
    }

    public void setAutorizacoes(String autorizacoes) {
        this.autorizacoes = autorizacoes;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
