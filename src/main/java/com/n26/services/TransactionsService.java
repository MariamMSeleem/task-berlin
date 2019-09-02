package com.n26.services;

import com.n26.dtos.StatisticsDTO;
import com.n26.dtos.StatisticsResponseDTO;
import com.n26.dtos.TransactionDTO;

import java.util.List;


public interface TransactionsService {
    void addTransaction(TransactionDTO transaction);

    void deleteAllTransactions();

    List<StatisticsDTO> getTransactionsWithinPeriod(int seconds);

    void calculateNewStats(StatisticsDTO statistics, StatisticsDTO statsPerSecond);
}
