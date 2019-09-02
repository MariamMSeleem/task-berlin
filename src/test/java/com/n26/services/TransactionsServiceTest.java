package com.n26.services;

import com.n26.dtos.StatisticsDTO;
import com.n26.dtos.TransactionDTO;
import com.n26.services.implementations.TransactionServiceImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class TransactionsServiceTest {

    @Autowired
    private TransactionsService transactionsService = new TransactionServiceImpl();

    @Before
    public void init(){
        ReflectionTestUtils.setField(transactionsService, "maxSeconds", 60);
    }

    @Test
    public void addTransactionIsOK(){
        Instant tenSecAgo = Instant.now().minusMillis(10000);
        Instant twentySecAgo = Instant.now().minusMillis(20000);
        TransactionDTO transactionTen = new TransactionDTO("12.5", tenSecAgo.toString());
        TransactionDTO transactionTwenty = new TransactionDTO("11.5", twentySecAgo.toString());
        transactionsService.addTransaction(transactionTen);
        transactionsService.addTransaction(transactionTwenty);
        List<StatisticsDTO> result = transactionsService.getTransactionsWithinPeriod(60);
        Assert.assertEquals(2, result.size());
    }

    @Test
    public void addTransactionSameTimeStampIsOK(){
        Instant tenSecAgo = Instant.now().minusMillis(10000);
        TransactionDTO transactionTen = new TransactionDTO("12.5", tenSecAgo.toString());
        TransactionDTO transactionTen2 = new TransactionDTO("11.5", tenSecAgo.toString());
        transactionsService.addTransaction(transactionTen);
        transactionsService.addTransaction(transactionTen2);
        List<StatisticsDTO> result = transactionsService.getTransactionsWithinPeriod(60);
        Assert.assertEquals(1, result.size());

    }

    @Test(expected = IllegalArgumentException.class)
    public void addTransactionOldTimeStampException(){
        Instant seventySecAgo = Instant.now().minusMillis(70000);
        TransactionDTO transactionTen = new TransactionDTO("12.5", seventySecAgo.toString());
        transactionsService.addTransaction(transactionTen);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void addTransactionFutureTimeStampException(){
        Instant tenSecLater = Instant.now().plusMillis(10000);
        TransactionDTO transactionTen = new TransactionDTO("12.5", tenSecLater.toString());
        transactionsService.addTransaction(transactionTen);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void addTransactionWrongFormatException(){
        Instant tenSecAgo = Instant.now().minusMillis(10000);
        TransactionDTO transactionTen = new TransactionDTO("True", tenSecAgo.toString());
        transactionsService.addTransaction(transactionTen);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void addTransactionWrongTimestampFormatException(){
        TransactionDTO transactionTen = new TransactionDTO("12.5", "Timestamp");
        transactionsService.addTransaction(transactionTen);
    }

    @Test
    public void deleteTransactionsIsOK(){
        Instant tenSecAgo = Instant.now().minusMillis(10000);
        Instant twentySecAgo = Instant.now().minusMillis(20000);
        TransactionDTO transactionTen = new TransactionDTO("12.5", tenSecAgo.toString());
        TransactionDTO transactionTwenty = new TransactionDTO("11.5", twentySecAgo.toString());
        transactionsService.addTransaction(transactionTen);
        transactionsService.addTransaction(transactionTwenty);
        transactionsService.deleteAllTransactions();
        List<StatisticsDTO> result = transactionsService.getTransactionsWithinPeriod(60);
        Assert.assertEquals(0, result.size());

    }

}
