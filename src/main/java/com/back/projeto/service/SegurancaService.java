package  com.back.projeto.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.back.projeto.entity.Usuario;
import com.back.projeto.repository.UsuarioRepository;

@Service
public class SegurancaService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
public UserDetails loadUserByUsername(String raMatricula) throws UsernameNotFoundException {

    Optional<Usuario> usuarioOp = usuarioRepo.findByRa_matricula(raMatricula);

    if (usuarioOp.isEmpty()) {
        throw new UsernameNotFoundException("Usuário não encontrado!");
    }

    Usuario usuario = usuarioOp.get();

    return User.builder().username(usuario.getRa_matricula()).password(usuario.getSenha()).authorities(usuario.getTipo_usuario())
            .build();
}

public boolean isPasswordValid(String password, Usuario usuario) {
    // Verifica se a senha é igual ao CPF do usuário
    if (password.equals(usuario.getCpf())) {
        return false; // Senha não pode ser igual ao CPF
    }

    // Verifica se a senha é a senha correta usando BCryptPasswordEncoder
    return passwordEncoder.matches(password, usuario.getSenha());
}


}
