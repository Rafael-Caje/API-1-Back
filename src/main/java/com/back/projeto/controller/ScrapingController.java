package com.back.projeto.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.back.projeto.service.ScrapingService;

@RestController
public class ScrapingController {

    @Autowired
    private ScrapingService scrapingService;

    @GetMapping("/scrape")
    public String scrapeData(@RequestParam(value = "manual", defaultValue = "false") boolean manual) {
        if (manual) {
            scrapingService.scrape();
            return "Scraping initiated manually!";
        } else {
            return "No action taken. Use ?manual=true to trigger scraping manually.";
        }
    }
}
