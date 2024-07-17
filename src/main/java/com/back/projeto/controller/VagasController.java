package com.back.projeto.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.back.projeto.entity.Vagas;
import com.back.projeto.service.VagaService;

@RestController
@RequestMapping("/vagas")
public class VagasController {
    
    @Autowired
    private VagaService service;

    @PostMapping
    public ResponseEntity<Vagas> criarVaga(@RequestBody Vagas vaga) {
        Vagas novaVaga = service.criarVaga(vaga);
        return new ResponseEntity<>(novaVaga, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Vagas>> buscarTodasVagas() {
        List<Vagas> vagas = service.buscarTodasVagas();
        return new ResponseEntity<>(vagas, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Vagas> buscarVagaPorId(@PathVariable Long id) {
        Vagas vaga = service.buscarVagaPorId(id);
        return new ResponseEntity<>(vaga, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Vagas> atualizarVaga(@PathVariable Long id, @RequestBody Vagas vagaAtualizada) {
        Vagas vaga = service.atualizarVaga(id, vagaAtualizada);
        return new ResponseEntity<>(vaga, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluirVaga(@PathVariable Long id) {
        service.excluirVaga(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
