package com.n26.controllers;

import com.n26.dtos.StatisticsResponseDTO;
import com.n26.services.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;


    @GetMapping("/statistics")
    public ResponseEntity<StatisticsResponseDTO> getStatistics(){
        StatisticsResponseDTO statisticsResponse = statisticsService.getStatistics();
        return new ResponseEntity(statisticsResponse, HttpStatus.OK);
    }

}
