package com.back.projeto.service;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.back.projeto.entity.Usuario;
import com.back.projeto.entity.Vagas;
import com.back.projeto.repository.UsuarioRepository;
import com.back.projeto.repository.VagasRepository;

@Service
public class VagaService {

    @Autowired 
    private VagasRepository vagasRepo;

    @Autowired
    private UsuarioRepository usuarioRepository;

    public Vagas criarVaga(Vagas vaga) {
        if (vaga.getUsuario() != null) {
            Usuario usuario = usuarioRepository.findById(vaga.getUsuario().getId())
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
            vaga.setUsuario(usuario);
        }

        vaga.setCreate_at(LocalDateTime.now());
        vaga.setUpdate_at(LocalDateTime.now());
        return vagasRepo.save(vaga);
    }

    public List<Vagas> listarVagasCriadasPorAdmins() {
        return vagasRepo.findByUsuarioTipoUsuario("ROLE_ADMIN");
    }

    
    public List<Vagas> buscarTodasVagas() {
        return vagasRepo.findAllOrderByCreate_atDesc();
    }


    public List<Vagas> buscarVagasPorNome(String nomeVaga) {
        return vagasRepo.findByNomeVagaContainingIgnoreCaseOrderByNomeVagaAsc(nomeVaga);
    }

    public Vagas buscarVagaPorId(Long id) {
        return vagasRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Vaga não encontrada"));
    }

    public Vagas atualizarVaga(Long id, Vagas vagaAtualizada) {
        Vagas vagaExistente = vagasRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Vaga não encontrada"));

        vagaExistente.setLocal_dado(vagaAtualizada.getLocal_dado());
        vagaExistente.setNome_vaga(vagaAtualizada.getNome_vaga());
        vagaExistente.setLocalizacao(vagaAtualizada.getLocalizacao());
        vagaExistente.setTipo_vaga(vagaAtualizada.getTipo_vaga());
        vagaExistente.setArea(vagaAtualizada.getArea());
        vagaExistente.setEmpresa(vagaAtualizada.getEmpresa());
        vagaExistente.setDescricao(vagaAtualizada.getDescricao());
        vagaExistente.setLink(vagaAtualizada.getLink());
        vagaExistente.setUpdate_at(LocalDateTime.now());

        return vagasRepo.save(vagaExistente);
    }

    public void excluirVaga(Long id) {
        if (!vagasRepo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Vaga não encontrada");
        }
        vagasRepo.deleteById(id);
    }
}
