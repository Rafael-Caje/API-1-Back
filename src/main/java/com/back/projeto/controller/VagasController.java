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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.back.projeto.entity.Vagas;
import com.back.projeto.service.VagaService;

@RestController
@RequestMapping("/vagas")
@Tag(name = "Vagas", description = "API para gerenciamento de vagas")
public class VagasController {
    
    @Autowired
    private VagaService service;

    @Operation(summary = "Criar uma nova vaga", description = "Cria uma nova vaga no sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Vaga criada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Erro ao criar a vaga")
    })
    @PostMapping
    public ResponseEntity<Vagas> criarVaga(@RequestBody Vagas vaga) {
        Vagas novaVaga = service.criarVaga(vaga);
        return new ResponseEntity<>(novaVaga, HttpStatus.CREATED);
    }

    @Operation(summary = "Buscar todas as vagas", description = "Retorna uma lista de todas as vagas")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de vagas retornada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Erro ao buscar as vagas")
    })
    @GetMapping
    public ResponseEntity<List<Vagas>> buscarTodasVagas() {
        List<Vagas> vagas = service.buscarTodasVagas();
        return new ResponseEntity<>(vagas, HttpStatus.OK);
    }

    @Operation(summary = "Buscar vaga por ID", description = "Retorna uma vaga específica pelo seu ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Vaga retornada com sucesso"),
        @ApiResponse(responseCode = "404", description = "Vaga não encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Vagas> buscarVagaPorId(@PathVariable Long id) {
        Vagas vaga = service.buscarVagaPorId(id);
        return new ResponseEntity<>(vaga, HttpStatus.OK);
    }

    @Operation(summary = "Atualizar uma vaga existente", description = "Atualiza uma vaga existente no sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Vaga atualizada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Erro ao atualizar a vaga")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Vagas> atualizarVaga(@PathVariable Long id, @RequestBody Vagas vagaAtualizada) {
        Vagas vaga = service.atualizarVaga(id, vagaAtualizada);
        return new ResponseEntity<>(vaga, HttpStatus.OK);
    }

    @Operation(summary = "Excluir uma vaga", description = "Exclui uma vaga pelo seu ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Vaga excluída com sucesso"),
        @ApiResponse(responseCode = "404", description = "Vaga não encontrada")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluirVaga(@PathVariable Long id) {
        service.excluirVaga(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
