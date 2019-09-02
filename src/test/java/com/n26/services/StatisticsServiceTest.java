package com.n26.services;

import com.n26.dtos.StatisticsDTO;
import com.n26.dtos.StatisticsResponseDTO;
import com.n26.services.implementations.StatisticsServiceImpl;
import com.n26.services.implementations.TransactionServiceImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class StatisticsServiceTest {

    @InjectMocks
    private StatisticsService statisticsService = new StatisticsServiceImpl();

    @Spy
    private TransactionsService transactionsService = new TransactionServiceImpl();

    @Before
    public void init(){
        ReflectionTestUtils.setField(statisticsService, "maxSeconds", 60);
    }

    @Test
    public void getStatisticsIsOK(){
        StatisticsDTO firstStats =  new StatisticsDTO(new BigDecimal(32), new BigDecimal(8), new BigDecimal(13), new BigDecimal(2), 4 );
        StatisticsDTO secondStats = new StatisticsDTO(new BigDecimal(12), new BigDecimal(6), new BigDecimal(11), new BigDecimal(1), 2 );
        List<StatisticsDTO> allStats = Arrays.asList(firstStats, secondStats);
        when(transactionsService.getTransactionsWithinPeriod(60)).thenReturn(allStats);
        StatisticsResponseDTO result = statisticsService.getStatistics();
        StatisticsResponseDTO expected = new StatisticsResponseDTO("44.00", "7.33", "13.00", "1.00", 6 );
        Assert.assertEquals(expected.getSum(), result.getSum());
        Assert.assertEquals(expected.getAvg(), result.getAvg());
        Assert.assertEquals(expected.getMax(), result.getMax());
        Assert.assertEquals(expected.getMin(), result.getMin());
        Assert.assertEquals(expected.getCount(), result.getCount());
    }

    @Test
    public void getStatisticsIsEmpty(){
        List<StatisticsDTO> allStats = new ArrayList<>();
        when(transactionsService.getTransactionsWithinPeriod(60)).thenReturn(allStats);
        StatisticsResponseDTO result = statisticsService.getStatistics();
        StatisticsResponseDTO expected = new StatisticsResponseDTO("0.00", "0.00", "0.00", "0.00", 0 );
        Assert.assertEquals(expected.getSum(), result.getSum());
        Assert.assertEquals(expected.getAvg(), result.getAvg());
        Assert.assertEquals(expected.getMax(), result.getMax());
        Assert.assertEquals(expected.getMin(), result.getMin());
        Assert.assertEquals(expected.getCount(), result.getCount());
    }
}
