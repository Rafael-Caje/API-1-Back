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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import com.back.projeto.entity.Usuario;
import com.back.projeto.repository.UsuarioRepository;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.web.multipart.MultipartFile;



@Service
public class UsuarioService {
    @Autowired
    private UsuarioRepository usuarioRepo;

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

}
