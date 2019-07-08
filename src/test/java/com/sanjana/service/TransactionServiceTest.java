package com.sanjana.service;

import com.sanjana.dao.TransactionDAO;
import com.sanjana.exception.NullTransactionException;
import com.sanjana.exception.TransactionDateInFutureException;
import com.sanjana.exception.TransactionOlderThanOneMinuteException;
import com.sanjana.vo.TransactionStatisticsVO;
import com.sanjana.vo.TransactionVO;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class TransactionServiceTest {

    @InjectMocks
    private TransactionService transactionService;

    @Mock
    private TransactionDAO transactionDAO;

    @Test(expected = TransactionOlderThanOneMinuteException.class)
    public void testCreateTransactionOlderThenOneMinute(){
        transactionService.createTransaction(BigDecimal.ZERO, Timestamp.valueOf("2019-06-14 20:11:00.257"));
    }

    @Test(expected = TransactionDateInFutureException.class)
    public void testCreateTransactionInFuture(){
        transactionService.createTransaction(BigDecimal.ZERO, Timestamp.valueOf("2019-06-17 20:11:00.257"));
    }

    @Test(expected = NullTransactionException.class)
    public void testNullAmountCreateTransaction(){
        transactionService.createTransaction(null, Timestamp.valueOf("2019-06-14 20:11:00.257"));
    }

    @Test(expected = NullTransactionException.class)
    public void testNullTimestampCreateTransaction(){
        transactionService.createTransaction(BigDecimal.ZERO, null);
    }

    @Test
    public void testValidCreateTransaction(){
        final BigDecimal amount = BigDecimal.TEN;
        final Timestamp timestamp = Timestamp.from(Instant.now());
        Mockito.doNothing().when(transactionDAO).createTransaction(amount,timestamp);
        transactionService.createTransaction(amount,timestamp);
    }

    @Test
    public void testDeleteTransaction(){
        transactionService.deleteAllTransactions();
        Mockito.verify(transactionDAO).deleteAllTransactions();
    }

    @Test
    public void testValidTransactionStatisticsWithInOneMinComputed(){
        Mockito.when(transactionDAO.getTransactions()).thenReturn(transactionsWithInOneMinToCompute());
        final TransactionStatisticsVO transactionStatisticsVOWithinOneMin = transactionService.getTransactionsStatisticsComputed();
        testAssertValues(transactionStatisticsVOWithinOneMin);
    }

    @Test
    public void testValidTransactionStatisticsWithOlderThenOneMinComputed(){
        Mockito.when(transactionDAO.getTransactions()).thenReturn(transactionsWithOlderThenOneMinToCompute());
        final TransactionStatisticsVO transactionStatisticsVO = transactionService.getTransactionsStatisticsComputed();
        testAssertValues(transactionStatisticsVO);
    }

    private List<TransactionVO> transactionsWithInOneMinToCompute(){
        final List<TransactionVO> transactionsListToCompute = new ArrayList<>();
        transactionsWithinOneMin(transactionsListToCompute);
        return transactionsListToCompute;
    }

    private List<TransactionVO> transactionsWithOlderThenOneMinToCompute(){
        final List<TransactionVO> transactionsListToCompute = new ArrayList<>();
        transactionsWithinOneMin(transactionsListToCompute);
        transactionsOlderThanOneMin(transactionsListToCompute);
        return transactionsListToCompute;
    }

    private void transactionsWithinOneMin(List<TransactionVO> transactionsListToCompute){
        for (int i=0; i<3; i++){
            final TransactionVO transactionVO = new TransactionVO();
            transactionVO.setAmount(new BigDecimal(i));
            transactionVO.setTimestamp(Timestamp.from(Instant.now()));
            transactionsListToCompute.add(transactionVO);
        }
    }

    private void transactionsOlderThanOneMin(List<TransactionVO> transactionsListToCompute){
        final TransactionVO transactionVO = new TransactionVO();
        transactionVO.setAmount(BigDecimal.TEN);
        transactionVO.setTimestamp(Timestamp.from(Instant.now().minusSeconds(61)));
        transactionsListToCompute.add(transactionVO);
    }

    private void testAssertValues(TransactionStatisticsVO transactionStatisticsVO){
        Assert.assertEquals("3.00",transactionStatisticsVO.getStrSum());
        Assert.assertEquals("1.00",transactionStatisticsVO.getStrAvg());
        Assert.assertEquals("2.00",transactionStatisticsVO.getStrMax());
        Assert.assertEquals("0.00",transactionStatisticsVO.getStrMin());
        Assert.assertEquals(3,transactionStatisticsVO.getCount());
    }
}
