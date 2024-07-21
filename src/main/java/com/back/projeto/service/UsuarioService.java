package com.back.projeto.service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
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

    @Autowired
    private EmailService emailService;

    private Map<String, VerificationCode> verificationCodes = new ConcurrentHashMap<>();

    public Optional<Usuario> buscarUsuarioPorId(Long id) {
        return usuarioRepo.findById(id);
    }

    public List<Usuario> buscarUsuariosPorNome(String nome) {
        return usuarioRepo.findByNomeContainingIgnoreCase(nome);
    }

    public Usuario criarUsuario(Usuario usuario) {
        if (usuario == null ||
                usuario.getCpf() == null || usuario.getCpf().isBlank() ||
                usuario.getRa_matricula() == null || usuario.getRa_matricula().isBlank() ||
                usuario.getTipo_usuario() == null || usuario.getTipo_usuario().isBlank() ||
                usuario.getEmail() == null || usuario.getEmail().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Dados invalidos");
        }
        usuario.setUpdate_at(LocalDateTime.now());
        String senha = usuario.getCpf();
        usuario.setSenha(senha);

        return usuarioRepo.save(usuario);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public List<Usuario> buscarTodosUsuarios() {
        return usuarioRepo.findAllOrderByNomeAsc();
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

    public ResponseEntity<Map<String, String>> verificarPrimeiroAcesso(String ra_matricula, String cpf) {
        if (ra_matricula == null || ra_matricula.isBlank() || cpf == null || cpf.isBlank()) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "RA/Matrícula e CPF são obrigatórios.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    
        Optional<Usuario> usuarioOptional = usuarioRepo.findByRa_matriculaAndCpf(ra_matricula, cpf);
        if (usuarioOptional.isEmpty()) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Ra ou CPF incorreto.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    
        Usuario usuario = usuarioOptional.get();
    
        if (!passwordEncoder.matches(cpf, usuario.getSenha())) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Usuário ja realizou o primeiro acesso");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    
        String token = generateToken(usuario);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Primeiro acesso validado.");
        response.put("token", token);
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<String> primeiraSenha(String token, String novaSenha) {
        if (token == null || token.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Token é obrigatório.");
        }

        if (novaSenha == null || novaSenha.isBlank() || novaSenha.length() < 8) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Nova senha é obrigatória e deve ter pelo menos 8 caracteres.");
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
        usuario.setSenha(novaSenha); // Sem criptografia
        usuarioRepo.save(usuario);

        return ResponseEntity.ok("Senha alterada com sucesso.");
    }

    public void enviarCodigoVerificacaoPorEmail(String email) {

        Optional<Usuario> optionalUsuario = usuarioRepo.findByEmail(email);
        if (optionalUsuario.isPresent()) {
            String codigoVerificacao = gerarCodigoVerificacao();

            verificationCodes.put(email, new VerificationCode(codigoVerificacao, Instant.now()));
            try {
                emailService.enviarEmail(email, "template_ri2kons", "35Ylun5ncXdGbClGT",
                        "{\"email\":\"" + email + "\", \"codigoVerificacao\":\"" + codigoVerificacao + "\"}");
            } catch (Exception e) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                        "Erro ao enviar email: " + e.getMessage());
            }
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado");
        }
    }

    private static class VerificationCode {
        private String code;
        private Instant createdAt;

        public VerificationCode(String code, Instant createdAt) {
            this.code = code;
            this.createdAt = createdAt;
        }

        public String getCode() {
            return code;
        }

        public Instant getCreatedAt() {
            return createdAt;
        }
    }

    public boolean verificarCodigoVerificacao(String email, String codigo) {
        VerificationCode verificationCode = verificationCodes.get(email);
        if (verificationCode != null && verificationCode.getCode().equals(codigo)) {

            return Duration.between(verificationCode.getCreatedAt(), Instant.now()).toMinutes() <= 5;
        }
        return false;
    }

    public void alterarSenha(String email, String codigo, String novaSenha) {

        if (verificarCodigoVerificacao(email, codigo)) {
            Optional<Usuario> optionalUsuario = usuarioRepo.findByEmail(email);
            if (optionalUsuario.isPresent()) {
                Usuario usuario = optionalUsuario.get();
                usuario.setSenha(novaSenha);
                usuarioRepo.save(usuario);

                verificationCodes.remove(email);
            } else {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado");
            }
        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Código de verificação inválido ou expirado");
        }
    }

    private String gerarCodigoVerificacao() {
        Random random = new Random();
        int codigo = 100000 + random.nextInt(900000);
        return String.valueOf(codigo);
    }


    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
    public ResponseEntity<String> alterarSenhaLogado(String ra_matricula, String senhaAntiga, String novaSenha) {
        Optional<Usuario> usuarioOptional = usuarioRepo.findByRa_matricula(ra_matricula);

        if (usuarioOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuário não encontrado");
        }

        Usuario usuario = usuarioOptional.get();

        if (!passwordEncoder.matches(senhaAntiga, usuario.getSenha())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Senha antiga incorreta");
        }

        if (novaSenha == null || novaSenha.isBlank() || novaSenha.length() < 8) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Nova senha deve ter pelo menos 8 caracteres.");
        }

        usuario.setSenha(novaSenha);
        usuarioRepo.save(usuario);

        return ResponseEntity.ok("Senha alterada com sucesso.");
    }
}




