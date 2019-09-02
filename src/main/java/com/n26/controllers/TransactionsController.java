package com.n26.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.n26.dtos.TransactionDTO;
import com.n26.services.TransactionsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
public class TransactionsController {

    @Autowired
    private TransactionsService transactionStatisticsService;

    @PostMapping("/transactions")
    public ResponseEntity addTransaction(@RequestBody String input) {

        try {
            final ObjectMapper mapper = new ObjectMapper();
            TransactionDTO transaction = mapper.readValue(input, TransactionDTO.class);
            transactionStatisticsService.addTransaction(transaction);
        } catch (IOException e) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        } catch (UnsupportedOperationException e) {
            return new ResponseEntity(HttpStatus.UNPROCESSABLE_ENTITY);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity(HttpStatus.CREATED);
    }


    @DeleteMapping("/transactions")
    public ResponseEntity deleteAllTransactions() {
        transactionStatisticsService.deleteAllTransactions();
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }
}
