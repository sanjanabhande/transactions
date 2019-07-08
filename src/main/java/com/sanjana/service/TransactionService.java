package com.sanjana.service;

import com.sanjana.dao.TransactionDAO;
import com.sanjana.exception.NullTransactionException;
import com.sanjana.exception.TransactionDateInFutureException;
import com.sanjana.exception.TransactionOlderThanOneMinuteException;
import com.sanjana.vo.TransactionStatisticsVO;
import com.sanjana.vo.TransactionVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.time.Duration;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

@Service
public class TransactionService {

    @Autowired
    private TransactionDAO transactionDAO;

    public void createTransaction(BigDecimal amount, Timestamp timestamp){
        validateToCreateTransaction(amount,timestamp);
        transactionDAO.createTransaction(amount,timestamp);
    }

    public List<TransactionVO> getTransactions(){
        return transactionDAO.getTransactions();
    }

    public TransactionStatisticsVO getTransactionsStatisticsComputed(){
        List<TransactionVO> transactionVOList = transactionDAO.getTransactions();
        if (transactionVOList.isEmpty()) return emptyComputations();
        getTransactionsWithInOneMin(transactionVOList);
        return getAllComputations(transactionVOList);
    }

    public void deleteAllTransactions(){
        transactionDAO.deleteAllTransactions();
    }

    private void validateToCreateTransaction(BigDecimal amount, Timestamp timestamp){
        if(null==amount || null==timestamp){
            throw new NullTransactionException();
        }
        Duration duration = Duration.between(timestamp.toInstant(),new Date().toInstant());
        validateTransactionOlderThanOneMin(duration);
        validateFutureTransaction(duration);
    }

    private void validateTransactionOlderThanOneMin(Duration duration){
        if ((duration.compareTo( Duration.ofSeconds(60) ) > 0))
            throw new TransactionOlderThanOneMinuteException();
    }

    private void validateFutureTransaction(Duration duration){
        if ((Duration.ofMillis(1).compareTo( duration ) > 0)) {
            throw new TransactionDateInFutureException();
        }
    }

    private void getTransactionsWithInOneMin(List<TransactionVO> transactionVOList){
        Iterator<TransactionVO> iterator = transactionVOList.iterator();
        while (iterator.hasNext()){
            TransactionVO transactionVO = iterator.next();
            Duration duration = Duration.between(transactionVO.getTimestamp().toInstant(),new Date().toInstant());
            if (duration.compareTo( Duration.ofSeconds(60) ) > 0)
                iterator.remove();
        }
    }

    private TransactionStatisticsVO getAllComputations(List<TransactionVO> transactionVOList){
        if (transactionVOList.isEmpty()) return emptyComputations();
        TransactionStatisticsVO transactionStatisticsVO = new TransactionStatisticsVO();
        transactionStatisticsVO.setStrSum(sumTransactionValues(transactionVOList).toString());
        transactionStatisticsVO.setStrAvg(avgTransactionValues(transactionVOList).toString());
        transactionStatisticsVO.setStrMax(highestTransactionValue(transactionVOList).toString());
        transactionStatisticsVO.setStrMin(lowestTransactionValue(transactionVOList).toString());
        transactionStatisticsVO.setCount(transactionVOList.size());
        return transactionStatisticsVO;
    }

    private BigDecimal sumTransactionValues(List<TransactionVO> transactionVOList){
        return transactionVOList.stream()
                .map(TransactionVO::getAmount)
                .reduce(BigDecimal::add)
                .get()
                .setScale(2,RoundingMode.HALF_UP);
    }

    private BigDecimal avgTransactionValues(List<TransactionVO> transactionVOList){
        return sumTransactionValues(transactionVOList)
                .divide(new BigDecimal(transactionVOList.size()),RoundingMode.HALF_UP)
                .setScale(2,RoundingMode.HALF_UP);
    }

    private BigDecimal highestTransactionValue(List<TransactionVO> transactionVOList){
        return transactionVOList.stream()
                .map(TransactionVO::getAmount)
                .reduce(BigDecimal::max)
                .get()
                .setScale(2,RoundingMode.HALF_UP);
    }

    private BigDecimal lowestTransactionValue(List<TransactionVO> transactionVOList){
        return transactionVOList.stream()
                .map(e->e.getAmount())
                .reduce(BigDecimal::min)
                .get()
                .setScale(2,RoundingMode.HALF_UP);
    }

    private TransactionStatisticsVO emptyComputations(){
        TransactionStatisticsVO transactionStatisticsVO = new TransactionStatisticsVO();
        transactionStatisticsVO.setStrSum(BigDecimal.ZERO.setScale(2,RoundingMode.HALF_UP).toString());
        transactionStatisticsVO.setStrAvg(BigDecimal.ZERO.setScale(2,RoundingMode.HALF_UP).toString());
        transactionStatisticsVO.setStrMax(BigDecimal.ZERO.setScale(2,RoundingMode.HALF_UP).toString());
        transactionStatisticsVO.setStrMin(BigDecimal.ZERO.setScale(2,RoundingMode.HALF_UP).toString());
        transactionStatisticsVO.setCount(0);
        return transactionStatisticsVO;
    }

}
