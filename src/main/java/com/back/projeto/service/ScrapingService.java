package com.back.projeto.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class ScrapingService {

    //@Scheduled(cron = "0 0 0 * * ?") // Executa todos os dias à meia-noite
    //@Scheduled(cron = "0 0 * * * ?") // Executa todas as horas
    //@Scheduled(cron = "0 * * * * ?") // Executa todos os minutos
    @Scheduled(cron = "0 50 2 * * ?") // Executa todos os dias às 02:50 da madrugada (Formato: 24h)

    public void scrape() {
        executeScraper();
    }

    public void executeScraper() {
        try {
            // Atualizar o repositório do script Python
            ProcessBuilder gitPull = new ProcessBuilder("git", "pull", "origin", "main");
            gitPull.directory(new File("/scraper"));
            gitPull.start().waitFor();

            // Executar o script Python
            ProcessBuilder pb = new ProcessBuilder("python3", "app.py");
            pb.directory(new File("/scraper"));
            Process process = pb.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            int exitCode = process.waitFor();
            System.out.println("Scraping script exited with code: " + exitCode);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
