package com.back.projeto.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.back.projeto.entity.Usuario;
import com.back.projeto.repository.UsuarioRepository;

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
}
