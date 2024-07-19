package com.back.projeto.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.back.projeto.repository.VagasRepository;

import java.time.LocalDateTime;

@Service
public class VagaCleanupService {

    @Autowired
    private VagasRepository vagasRepo;

    // Aqui está uma breve explicação dos campos:

    // 0 — Segundo
    // 43 — Minuto
    // 12 — Hora
    // * — Dia do mês (qualquer dia do mês)
    // * — Mês (qualquer mês)
    // ? — Dia da semana (não especificado)
    // Este método será executado diariamente às 01:00
    // @Scheduled(cron = "0 0 1 * * ?")
    @Scheduled(cron = "0 00 00 * * ?")
    public void excluirVagasAntigas() {
        LocalDateTime dataLimite = LocalDateTime.now().minusDays(30);
        vagasRepo.deleteByCreate_atBefore(dataLimite);
    }

}
