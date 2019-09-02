package com.n26.services.implementations;

import com.n26.dtos.StatisticsDTO;
import com.n26.dtos.StatisticsResponseDTO;
import com.n26.dtos.TransactionDTO;
import com.n26.services.TransactionsService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Service
public class TransactionServiceImpl implements TransactionsService {


    private Map<Instant, StatisticsDTO> transactions = new ConcurrentHashMap<>();
    private ReadWriteLock lock = new ReentrantReadWriteLock();

    @Value("${max.seconds}")
    private int maxSeconds;

    @Override
    public void addTransaction(TransactionDTO transaction) {
        Instant now = Instant.now();
        Instant timestamp;
        BigDecimal amount;
        try{
            timestamp = Instant.parse(transaction.getTimestamp());
            amount = new BigDecimal(transaction.getAmount());
        }
        catch (NumberFormatException | DateTimeParseException e){
            throw  new UnsupportedOperationException();
        }

        Duration duration = Duration.between(now, timestamp);
        if(timestamp.compareTo(now) > 0)
            throw  new UnsupportedOperationException();
        else if(duration.abs().getSeconds() >= maxSeconds){
            throw new IllegalArgumentException();
        }

        //To Lock hashmap for each thread while removing and writing to it
        Lock writeLock = lock.writeLock();
        try {
            writeLock.lock();

            /*Removing old transactions before adding a new one will always ensure that the hashmap size
            is never greater than maxSeconds (60 seconds in this case). Ensuring O(1) memory.
            */
            transactions.entrySet()
                    .removeIf(entry -> (Duration.between(now, entry.getKey()).abs().getSeconds() >= maxSeconds));

            StatisticsDTO statsPerSecond = transactions.getOrDefault(timestamp.truncatedTo(ChronoUnit.SECONDS), new StatisticsDTO());
            calculateNewStats(statsPerSecond, new StatisticsDTO(amount, amount, amount, amount, 1L));
            transactions.put(timestamp.truncatedTo(ChronoUnit.SECONDS), statsPerSecond);
        }
        finally{
            writeLock.unlock();
        }
    }


    @Override
    public void deleteAllTransactions() {
        transactions.clear();
    }

    @Override
    public List<StatisticsDTO> getTransactionsWithinPeriod(int seconds) {

        List<StatisticsDTO> allStatsPerSecond = new ArrayList<>();
        Instant now = Instant.now();

        //To Lock hashmap for each thread while reading from it
        Lock readLock = lock.readLock();
        try {
            readLock.lock();

            /*Removing old transactions will always ensure that the hashmap size
            is never greater than maxSeconds (60 seconds in this case)
            */
            transactions.entrySet()
                    .removeIf(entry -> (Duration.between(now, entry.getKey()).abs().getSeconds() >= seconds));
            for (Map.Entry<Instant, StatisticsDTO> entry : transactions.entrySet()) {
                StatisticsDTO statsPerSecond = entry.getValue();
                allStatsPerSecond.add(statsPerSecond);
            }
        }
        finally {
            readLock.unlock();
        }
        return allStatsPerSecond;
    }

    @Override
    public void calculateNewStats(StatisticsDTO statistics, StatisticsDTO statsPerSecond) {
        statistics.setCount(statistics.getCount() + statsPerSecond.getCount());
        statistics.setSum(statistics.getSum().add(statsPerSecond.getSum()));
        statistics.setMax(statsPerSecond.getMax().max(statistics.getMax()));
        statistics.setMin(statsPerSecond.getMin().min(statistics.getMin()));
        statistics.setAvg(statistics.getSum().divide(new BigDecimal(statistics.getCount()), 2, RoundingMode.HALF_UP));
    }




}
