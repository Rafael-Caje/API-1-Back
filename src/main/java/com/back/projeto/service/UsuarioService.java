package com.back.projeto.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Date;
import java.security.Key;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.back.projeto.dto.UsuarioPerfilDTO;
import com.back.projeto.entity.Usuario;
import com.back.projeto.repository.UsuarioRepository;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import org.springframework.web.multipart.MultipartFile;

@Service
public class UsuarioService {
    @Autowired
    private UsuarioRepository usuarioRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    public Usuario criarUsuario(Usuario usuario) {
        if (usuario == null ||
                usuario.getCpf() == null || usuario.getCpf().isBlank() ||
                usuario.getRa_matricula() == null || usuario.getRa_matricula().isBlank() ||
                usuario.getTipo_usuario() == null || usuario.getTipo_usuario().isBlank() ||
                usuario.getEmail() == null || usuario.getEmail().isBlank() ||
                usuario.getSenha() == null || usuario.getSenha().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Dados invalidos");
        }
        return usuarioRepo.save(usuario);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public List<Usuario> buscarTodosUsuarios() {
        return usuarioRepo.findAll();
    }

    public Usuario atualizarUsuario(Long id, Usuario usuarioAtualizado) {
        Optional<Usuario> usuarioOptional = usuarioRepo.findById(id);
        if (usuarioOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado");
        }

        Usuario usuarioExistente = usuarioOptional.get();

        if (usuarioAtualizado.getCpf() != null && !usuarioAtualizado.getCpf().isBlank()) {
            usuarioExistente.setCpf(usuarioAtualizado.getCpf());
        }
        if (usuarioAtualizado.getRa_matricula() != null && !usuarioAtualizado.getRa_matricula().isBlank()) {
            usuarioExistente.setRa_matricula(usuarioAtualizado.getRa_matricula());
        }
        if (usuarioAtualizado.getNome() != null && !usuarioAtualizado.getNome().isBlank()) {
            usuarioExistente.setNome(usuarioAtualizado.getNome());
        }
        if (usuarioAtualizado.getTipo_usuario() != null && !usuarioAtualizado.getTipo_usuario().isBlank()) {
            usuarioExistente.setTipo_usuario(usuarioAtualizado.getTipo_usuario());
        }
        if (usuarioAtualizado.getEmail() != null && !usuarioAtualizado.getEmail().isBlank()) {
            usuarioExistente.setEmail(usuarioAtualizado.getEmail());
        }
        if (usuarioAtualizado.getSenha() != null && !usuarioAtualizado.getSenha().isBlank()) {
            usuarioExistente.setSenha(usuarioAtualizado.getSenha());
        }
        usuarioExistente.setUpdate_at(LocalDateTime.now());

        return usuarioRepo.save(usuarioExistente);
    }

    public void excluirUsuario(Long id) {
        if (!usuarioRepo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado");
        }
        usuarioRepo.deleteById(id);
    }

    public void cadastrarUsuariosViaCSV(MultipartFile file) {
        System.out.println("Iniciando o processamento do arquivo CSV.");
        try (InputStream is = file.getInputStream();
                BufferedReader fileReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                CSVParser csvParser = new CSVParser(fileReader, CSVFormat.DEFAULT.withHeader())) {

            Map<String, String[]> fieldVariations = new HashMap<>();
            fieldVariations.put("cpf", new String[] { "cpf" });
            fieldVariations.put("nome", new String[] { "nome", "nome aluno" });
            fieldVariations.put("ra_matricula", new String[] { "ra_matricula", "ra" });
            fieldVariations.put("email", new String[] { "email", "e-mail", "mail" });

            Map<String, String> headerMappings = new HashMap<>();
            Map<String, Integer> headerMap = csvParser.getHeaderMap();

            for (String field : fieldVariations.keySet()) {
                for (String variation : fieldVariations.get(field)) {
                    for (String header : headerMap.keySet()) {
                        if (header.equalsIgnoreCase(variation)) {
                            headerMappings.put(field, header);
                            break;
                        }
                    }
                    if (headerMappings.containsKey(field)) {
                        break;
                    }
                }
            }

            for (CSVRecord csvRecord : csvParser) {
                try {
                    String cpf = csvRecord.get(headerMappings.get("cpf"));
                    String nome = csvRecord.get(headerMappings.get("nome"));
                    String raMatricula = csvRecord.get(headerMappings.get("ra_matricula"));
                    String email = csvRecord.get(headerMappings.get("email"));

                    Usuario usuario = new Usuario();
                    usuario.setCpf(cpf);
                    usuario.setNome(nome);
                    usuario.setRa_matricula(raMatricula);
                    usuario.setEmail(email);
                    usuario.setTipo_usuario("ROLE_USER");
                    usuario.setSenha(cpf);

                    try {
                        criarUsuario(usuario);
                    } catch (ResponseStatusException e) {
                        System.err.println("Erro ao criar usuário para CPF " + cpf + ": " + e.getMessage());
                    }

                } catch (Exception e) {
                    System.err.println("Erro ao processar linha do CSV: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Erro ao processar o arquivo CSV", e);
        }
    }

    public Usuario buscarUsuarioPorRaMatricula(String ra_matricula) {
        Optional<Usuario> usuarioOptional = usuarioRepo.findByRa_matricula(ra_matricula);
        if (usuarioOptional.isPresent()) {
            return usuarioOptional.get();
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado");
        }
    }

    public UsuarioPerfilDTO converterParaUsuarioPerfilDTO(Usuario usuario) {
        UsuarioPerfilDTO dto = new UsuarioPerfilDTO();
        dto.setId(usuario.getId());
        dto.setCpf(usuario.getCpf());
        dto.setRa_matricula(usuario.getRa_matricula());
        dto.setNome(usuario.getNome());
        dto.setTipo_usuario(usuario.getTipo_usuario());
        dto.setEmail(usuario.getEmail());
        dto.setUpdate_at(usuario.getUpdate_at());
        return dto;
    }

    public String generateToken(Usuario usuario) {
        return Jwts.builder()
                .setSubject(usuario.getRa_matricula())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000)) // 1 day expiration
                .signWith(key)
                .compact();
    }

public ResponseEntity<String> verificarPrimeiroAcesso(String ra_matricula, String cpf) {
    if (ra_matricula == null || ra_matricula.isBlank() || cpf == null || cpf.isBlank()) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("RA/Matrícula e CPF são obrigatórios.");
    }

    Optional<Usuario> usuarioOptional = usuarioRepo.findByRa_matriculaAndCpf(ra_matricula, cpf);
    if (usuarioOptional.isEmpty()) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Ra ou CPF incorreto.");
    }

    Usuario usuario = usuarioOptional.get();

    if (!passwordEncoder.matches(cpf, usuario.getSenha())) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuário ja realizou o primeiro acesso");
    }
    String token = generateToken(usuario);
    return ResponseEntity.ok("Primeiro acesso validado. Token gerado: " + token);
}


public ResponseEntity<String> primeiraSenha(String token, String novaSenha) {
    if (token == null || token.isBlank()) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Token é obrigatório.");
    }

    if (novaSenha == null || novaSenha.isBlank() || novaSenha.length() < 8) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Nova senha é obrigatória e deve ter pelo menos 8 caracteres.");
    }

    Claims claims;
    try {
        claims = Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token)
            .getBody();
    } catch (JwtException e) {
        String errorMessage = "Token inválido";
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage);
    }

    String raMatricula = claims.getSubject();
    Optional<Usuario> usuarioOptional = usuarioRepo.findByRa_matricula(raMatricula);
    if (usuarioOptional.isEmpty()) {
        String errorMessage = "Usuário não encontrado para RA/Matrícula: " + raMatricula;
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);
    }

    Usuario usuario = usuarioOptional.get();
    usuario.setSenha(passwordEncoder.encode(novaSenha));
    usuarioRepo.save(usuario);

    return ResponseEntity.ok("Senha alterada com sucesso.");
}
    
}
