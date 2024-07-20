package com.back.projeto.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.back.projeto.entity.Usuario;
import com.back.projeto.service.UsuarioService;

@RestController
@CrossOrigin
@RequestMapping("/user")
@Tag(name = "Usuario", description = "API para gerenciamento de usuários")
public class UsuarioController {

    @Autowired
    private UsuarioService service;

    @Operation(summary = "Criar um novo usuário", description = "Cria um novo usuário no sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuário criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Erro ao criar o usuário")
    })
    @PostMapping
    public ResponseEntity<Usuario> criarUsuario(@RequestBody Usuario usuario) {
        Usuario novoUsuario = service.criarUsuario(usuario);
        return new ResponseEntity<>(novoUsuario, HttpStatus.CREATED);
    }

    @Operation(summary = "Buscar todos os usuários", description = "Retorna uma lista de todos os usuários cadastrados")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de usuários retornada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Erro ao buscar os usuários")
    })
    @GetMapping
    public ResponseEntity<List<Usuario>> buscarTodosUsuarios() {
        List<Usuario> usuarios = service.buscarTodosUsuarios();
        return new ResponseEntity<>(usuarios, HttpStatus.OK);
    }

    @Operation(summary = "Atualizar um usuário existente", description = "Atualiza um usuário existente pelo seu ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Erro ao atualizar o usuário")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Usuario> atualizarUsuario(@PathVariable Long id, @RequestBody Usuario usuarioAtualizado) {
        Usuario usuario = service.atualizarUsuario(id, usuarioAtualizado);
        return new ResponseEntity<>(usuario, HttpStatus.OK);
    }

    @Operation(summary = "Excluir um usuário", description = "Exclui um usuário pelo seu ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Usuário excluído com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluirUsuario(@PathVariable Long id) {
        service.excluirUsuario(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(summary = "Criar novos usuários apartir do CSV", description = "Cria novos usuários apartir do CSV no sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuários criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Erro ao criar os usuários")
    })

    @PostMapping("/upload-csv")
    public ResponseEntity<Void> uploadCSV(@RequestParam("file") MultipartFile file) {
        service.cadastrarUsuariosViaCSV(file);
        return ResponseEntity.ok().build();
    }
}