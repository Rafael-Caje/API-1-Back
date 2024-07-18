package  com.back.projeto.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.back.projeto.entity.Usuario;
import com.back.projeto.repository.UsuarioRepository;

@Service
public class SegurancaService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepo;

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


}
