package com.back.projeto.security;

import java.util.Date;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.IOException;
import io.jsonwebtoken.security.Keys;

public class JwtUtils {

    private static final String KEY = "br.gov.sp.fatec.springbootexample";

    public static String generateToken(Authentication authentication) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        Login login = new Login();
        login.setRa_matricula(authentication.getName());
        login.setEmail(authentication.getName());  // Corrigido para incluir o e-mail
        if (!authentication.getAuthorities().isEmpty()) {
            login.setAutorizacoes(authentication.getAuthorities().iterator().next().getAuthority());
        }

        String loginJson = mapper.writeValueAsString(login);

        Date agora = new Date();
        Long hora = 1000L * 60L * 60L; // Uma hora

        return Jwts.builder()
                .claim("userDetails", loginJson)
                .setIssuer("br.gov.sp.fatec")
                .setSubject(authentication.getName())
                .setExpiration(new Date(agora.getTime() + hora))
                .signWith(Keys.hmacShaKeyFor(KEY.getBytes()), SignatureAlgorithm.HS256).compact();
    }

    public static Authentication parseToken(String token)
        throws IOException, JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        String credentialsJson = Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(KEY.getBytes())).build()
                .parseClaimsJws(token).getBody().get("userDetails", String.class);
        Login usuario = mapper.readValue(credentialsJson, Login.class);

        // Converte a lista de autorizações para um array de String
        String[] authoritiesArray = { usuario.getAutorizacoes() };

        UserDetails userDetails = User.builder()
                .username(usuario.getRa_matricula())
                .password("secret")
                .authorities(authoritiesArray).build();

        return new UsernamePasswordAuthenticationToken(usuario.getRa_matricula(), null, userDetails.getAuthorities());
    }
}
