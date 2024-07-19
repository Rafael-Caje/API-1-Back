package com.back.projeto.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.back.projeto.entity.Usuario;
import com.back.projeto.repository.UsuarioRepository;
import com.back.projeto.security.JwtUtils;
import com.back.projeto.security.Login;

@RestController
@CrossOrigin
@RequestMapping(value = "/login")
public class LoginController {

    @Autowired
    private AuthenticationManager authManager;

    @Autowired
    private UsuarioRepository usuarioRepo;

    @PostMapping
    public Login autenticar(@RequestBody Login login) throws JsonProcessingException {
        Authentication auth = new UsernamePasswordAuthenticationToken(login.getRa_matricula(), login.getSenha());
        auth = authManager.authenticate(auth);

        // Obtém a matrícula do usuário autenticado
        String raMatricula = auth.getName();

        // Busca o usuário no banco de dados pela matrícula
        Usuario usuarioAutenticado = usuarioRepo.findByRa_matricula(raMatricula)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado!"));

        // Cria um objeto Login para retorno
        Login respostaLogin = new Login();
        respostaLogin.setId(usuarioAutenticado.getId());
        respostaLogin.setRa_matricula(usuarioAutenticado.getRa_matricula());
        respostaLogin.setEmail(usuarioAutenticado.getEmail());
        respostaLogin.setAutorizacoes(usuarioAutenticado.getTipo_usuario());
        respostaLogin.setToken(JwtUtils.generateToken(auth));
        respostaLogin.setSenha(null); // Limpa a senha antes de retornar

        return respostaLogin;
    }
}
