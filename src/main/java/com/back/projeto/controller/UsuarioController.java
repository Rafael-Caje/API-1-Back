package com.back.projeto.controller;


import java.util.List;
import java.util.Optional;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.core.Authentication;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.back.projeto.dto.PrimeiroAcessoDTO;
import com.back.projeto.dto.SenhaTokenDTO;

import com.back.projeto.dto.UsuarioPerfilDTO;
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

    @Operation(summary = "Buscar todos os usuários por nome", description = "Retorna uma lista de todos os usuários cadastrados por nome")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de usuários retornada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Erro ao buscar os usuários")
    })
    @PostMapping("/buscar-por-nome")
    public List<Usuario> buscarUsuariosPorNome(@RequestParam String nome) {
        return service.buscarUsuariosPorNome(nome);
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


    @Operation(summary = "Buscar apenas um usuário", description = "Retorna um usuário cadastrado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de usuários retornada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Erro ao buscar os usuários")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Usuario> buscarUsuarioPorId(@PathVariable Long id) {
        Optional<Usuario> usuario = service.buscarUsuarioPorId(id);
        if (usuario.isPresent()) {
            return new ResponseEntity<>(usuario.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
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

    @GetMapping("/profile")
    public UsuarioPerfilDTO getPerfilUsuario(Authentication authentication) {
        String raMatricula = authentication.getName();
        Usuario usuario = service.buscarUsuarioPorRaMatricula(raMatricula);
        return service.converterParaUsuarioPerfilDTO(usuario);
    }

    @Operation(summary = "Primeiro acesso", description = "Primeiro acesso de usuarios")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Primeiro acesso bem sucedido "),
            @ApiResponse(responseCode = "400", description = "Erro no primeiro acesso")
    })

    @PostMapping("/primeiro-acesso")
    public ResponseEntity<String> verificarPrimeiroAcesso(@RequestBody PrimeiroAcessoDTO request) {
        return service.verificarPrimeiroAcesso(request.getRa_matricula(), request.getCpf());
    }


    @Operation(summary = "Primeira senha", description = "Criacao de senha")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Senha criada com suceso "),
            @ApiResponse(responseCode = "400", description = "Erro ao criar a senha")
    })

    @PostMapping("/senha-token")
    public ResponseEntity<String> primeiraSenha(@RequestBody SenhaTokenDTO request) {
        return service.primeiraSenha(request.getToken(), request.getNovaSenha());
    }

    @Operation(summary = "Enviar codigo de verificação no E-mail", description = "Envio de codigo de verificação no e-mail")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "E-mail enviado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Erro ao enviar o e-mail")
    })
    @PostMapping("/enviar-codigo-verificacao")
    public ResponseEntity<?> enviarCodigoVerificacaoPorEmail(@RequestBody Map<String, String> requestBody) {
        String email = requestBody.get("email");
        if (email == null || email.isEmpty()) {
            return ResponseEntity.badRequest().body("O email não foi fornecido no corpo da solicitação.");
        }

        try {
            service.enviarCodigoVerificacaoPorEmail(email);
            return ResponseEntity.ok("Código de verificação enviado com sucesso.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao enviar código de verificação: " + e.getMessage());
        }
    }

    @Operation(summary = "Validação de codigo enviado ao e-mail", description = "Validação do codigo enviado ao e-mail")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Codigo Validado com Sucesso"),
            @ApiResponse(responseCode = "400", description = "Erro ao validar o codigo")
    })
    @PostMapping("/verificar-codigo-verificacao")
    public ResponseEntity<?> verificarCodigoVerificacao(@RequestBody Map<String, String> requestBody) {
        String email = requestBody.get("email");
        String codigo = requestBody.get("codigo");

        if (email == null || email.isEmpty() || codigo == null || codigo.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body("O email e o código de verificação devem ser fornecidos no corpo da solicitação.");
        }

        try {
            boolean codigoValido = service.verificarCodigoVerificacao(email, codigo);
            if (codigoValido) {
                return ResponseEntity.ok("Código de verificação válido.");
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Código de verificação inválido.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao verificar código de verificação: " + e.getMessage());
        }
    }

    @Operation(summary = "Alteração de Senha", description = "Alteração de Senha")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Senha Alterada com Sucesso"),
            @ApiResponse(responseCode = "400", description = "Erro ao Alterar a Senha")
    })
    @PutMapping("/alterar-senha")
    public ResponseEntity<?> alterarSenha(@RequestBody Map<String, String> requestBody) {
        String email = requestBody.get("email");
        String codigo = requestBody.get("codigo");
        String novaSenha = requestBody.get("novaSenha");

        if (email == null || email.isEmpty() || codigo == null || codigo.isEmpty() || novaSenha == null
                || novaSenha.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body("O email, código de verificação e nova senha devem ser fornecidos no corpo da solicitação.");
        }

        try {
            service.alterarSenha(email, codigo, novaSenha);
            return ResponseEntity.ok("Senha alterada com sucesso.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao alterar senha: " + e.getMessage());
        }
    }


}