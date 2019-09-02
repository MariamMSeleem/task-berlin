package com.n26.services.implementations;

import com.n26.dtos.StatisticsDTO;
import com.n26.dtos.StatisticsResponseDTO;
import com.n26.services.StatisticsService;
import com.n26.services.TransactionsService;
import org.omg.IOP.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class StatisticsServiceImpl implements StatisticsService {

    @Autowired
    private TransactionsService transactionsService;

    @Value("${max.seconds}")
    private int maxSeconds;

    @Override
    public StatisticsResponseDTO getStatistics() {

        List<StatisticsDTO> allStatsPerSecond = transactionsService.getTransactionsWithinPeriod(maxSeconds);

        StatisticsDTO statistics = new StatisticsDTO();

        allStatsPerSecond.forEach(statsPerSecond ->  transactionsService.calculateNewStats(statistics, statsPerSecond));

        roundTo2DecimalPoints(statistics);
        return new StatisticsResponseDTO(statistics.getSum().toString(), statistics.getAvg().toString(),
                statistics.getMax().toString(), statistics.getMin().toString(),
                statistics.getCount());
    }

    private void roundTo2DecimalPoints(StatisticsDTO statistics){
        statistics.setMax(statistics.getMax().equals(new BigDecimal(Integer.MIN_VALUE))?
                new BigDecimal("0.00").setScale(2, RoundingMode.HALF_UP): statistics.getMax().setScale(2, RoundingMode.HALF_UP));
        statistics.setMin(statistics.getMin().equals(new BigDecimal(Integer.MAX_VALUE))?
                new BigDecimal("0.00").setScale(2, RoundingMode.HALF_UP): statistics.getMin().setScale(2, RoundingMode.HALF_UP));
        statistics.setSum(statistics.getSum().setScale(2, RoundingMode.HALF_UP));
        statistics.setAvg(statistics.getAvg().setScale(2, RoundingMode.HALF_UP));
    }
}
