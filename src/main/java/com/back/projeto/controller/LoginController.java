package com.back.projeto.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import com.back.projeto.entity.Usuario;
import com.back.projeto.repository.UsuarioRepository;
import com.back.projeto.security.JwtUtils;
import com.back.projeto.security.Login;
import com.back.projeto.service.SegurancaService;

@RestController
@CrossOrigin
@RequestMapping(value = "/login")
public class LoginController {

    @Autowired
    private AuthenticationManager authManager;

    @Autowired
    private UsuarioRepository usuarioRepo;

    @Autowired
    private SegurancaService segurancaService;

    @PostMapping
    public ResponseEntity<?> autenticar(@RequestBody Login login) {
        try {
            // Tenta autenticar o usuário
            Authentication auth = new UsernamePasswordAuthenticationToken(login.getRa_matricula(), login.getSenha());
            auth = authManager.authenticate(auth);

            // Obtém a matrícula do usuário autenticado
            String raMatricula = auth.getName();

            // Busca o usuário no banco de dados pela matrícula
            Usuario usuarioAutenticado = usuarioRepo.findByRa_matricula(raMatricula)
                    .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado!"));

            // Verifica se a senha fornecida é válida
            if (!segurancaService.isPasswordValid(login.getSenha(), usuarioAutenticado)) {
                throw new BadCredentialsException("Senha inválida ou senha não pode ser igual ao CPF.");
            }

            // Cria um objeto Login para retorno
            Login respostaLogin = new Login();
            respostaLogin.setId(usuarioAutenticado.getId());
            respostaLogin.setRa_matricula(usuarioAutenticado.getRa_matricula());
            respostaLogin.setEmail(usuarioAutenticado.getEmail());
            respostaLogin.setAutorizacoes(usuarioAutenticado.getTipo_usuario());
            respostaLogin.setToken(JwtUtils.generateToken(auth));
            respostaLogin.setSenha(null); // Limpa a senha antes de retornar

            return ResponseEntity.ok(respostaLogin);

        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse("Usuário não encontrado!"));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse("Senha inválida ou senha não pode ser igual ao CPF."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("Erro inesperado. Tente novamente mais tarde."));
        }
    }

    // Classe interna para a resposta de erro
    public static class ErrorResponse {
        private String mensagem;

        public ErrorResponse(String mensagem) {
            this.mensagem = mensagem;
        }

        public String getMensagem() {
            return mensagem;
        }

        public void setMensagem(String mensagem) {
            this.mensagem = mensagem;
        }
    }
}
