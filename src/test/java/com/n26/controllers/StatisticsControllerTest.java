package com.n26.controllers;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.n26.dtos.StatisticsResponseDTO;
import com.n26.services.StatisticsService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.Charset;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = StatisticsController.class, secure = false)
public class StatisticsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StatisticsService statisticsService;

    @Autowired
    private ObjectMapper objectMapper;

    private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));


    @Test
    public void getStatisticsIsOK() throws Exception{
        StatisticsResponseDTO statisticsResponseDTO = new StatisticsResponseDTO();
        when(statisticsService.getStatistics()).thenReturn(statisticsResponseDTO);
        mockMvc.perform(get("/statistics").contentType(contentType))
                .andExpect(status().isOk());
    }



}
