package com.n26.controllers;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.n26.dtos.StatisticsResponseDTO;
import com.n26.dtos.TransactionDTO;
import com.n26.services.TransactionsService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.Charset;
import java.time.Instant;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = TransactionsController.class, secure = false)
public class TransactionsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransactionsService transactionsService;

    private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));


    @Test
    public void addTransactionTestIsCreated() throws Exception{

        String transaction = "{\"amount\":\"12.5\", \"timestamp\": \"2019-09-01T18:09:0.23Z\"}";
        doNothing().when(transactionsService).addTransaction(any(TransactionDTO.class));

        mockMvc.perform(post("/transactions").contentType(contentType).content(transaction))
                .andExpect(status().isCreated());
    }

    @Test
    public void addTransactionTestInFutureIsUnProcessable() throws Exception{
        Instant now = Instant.now();
        String future = now.plusMillis(600000).toString();
        String transaction = "{\"amount\":\"12.5\", \"timestamp\": \""+ future + "\"}";
        doThrow(new UnsupportedOperationException()).when(transactionsService).addTransaction(any(TransactionDTO.class));

        mockMvc.perform(post("/transactions").contentType(contentType).content(transaction))
                .andExpect(status().isUnprocessableEntity());
    }


    @Test
    public void addTransactionTestInPastIsNoContent() throws Exception{
        Instant now = Instant.now();
        String future = now.minusMillis(600000).toString();
        String transaction = "{\"amount\":\"12.5\", \"timestamp\": \""+ future + "\"}";
        doThrow(new IllegalArgumentException()).when(transactionsService).addTransaction(any(TransactionDTO.class));

        mockMvc.perform(post("/transactions").contentType(contentType).content(transaction))
                .andExpect(status().isNoContent());
    }

    @Test
    public void addTransactionTestBadJSONIsBadRequest() throws Exception{
        String transaction = "{\"amount_\":\"12.5\", \"timestamp_\": \"2019-09-01T18:09:0.23Z\"}";

        mockMvc.perform(post("/transactions").contentType(contentType).content(transaction))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void deleteTransactionsIsNoContent() throws Exception{
        doNothing().when(transactionsService).deleteAllTransactions();

        mockMvc.perform(delete("/transactions").contentType(contentType))
                .andExpect(status().isNoContent());
    }

}
