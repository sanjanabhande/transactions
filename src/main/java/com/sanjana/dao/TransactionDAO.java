package com.sanjana.dao;

import com.sanjana.vo.TransactionVO;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Repository
public class TransactionDAO {

    private static final List<TransactionVO> transactionVOList = new ArrayList<>();

    public void createTransaction(BigDecimal amount, Timestamp timestamp){
        TransactionVO transactionVO = new TransactionVO();
        transactionVO.setAmount(amount);
        transactionVO.setTimestamp(timestamp);
        transactionVOList.add(transactionVO);
    }

    public List<TransactionVO> getTransactions(){
        return transactionVOList;
    }

    public void deleteAllTransactions(){
        transactionVOList.clear();
    }

}
