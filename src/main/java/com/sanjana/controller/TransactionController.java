package com.sanjana.controller;

import com.sanjana.exception.NullTransactionException;
import com.sanjana.exception.TransactionDateInFutureException;
import com.sanjana.exception.TransactionOlderThanOneMinuteException;
import com.sanjana.service.TransactionService;
import com.sanjana.vo.TransactionStatisticsVO;
import com.sanjana.vo.TransactionVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private TransactionService transactionService1;

    @PostMapping(value = "/transactions")
    public ResponseEntity<String> createTransaction(@RequestBody TransactionVO transactionVO){
        try {
            if(null==transactionVO.getAmount() || null==transactionVO.getTimestamp()){
             throw new NullTransactionException();
            }
            transactionService.createTransaction(transactionVO.getAmount(),transactionVO.getTimestamp());
            return new ResponseEntity<>(HttpStatus.CREATED);
        }catch (TransactionOlderThanOneMinuteException ex){
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }catch (TransactionDateInFutureException | NullTransactionException e){
            return new ResponseEntity<>(HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    @GetMapping(value = "/statistics")
    public ResponseEntity<TransactionStatisticsVO> getTransactionsStatisticsComputed(){
        return new ResponseEntity<>(transactionService.getTransactionsStatisticsComputed(), HttpStatus.OK);
    }

    @GetMapping(value = "/transactions")
    public ResponseEntity<List<TransactionVO>> getTransactions(){
        return new ResponseEntity<>(transactionService.getTransactions(), HttpStatus.OK);
    }

    @DeleteMapping(value = "/transactions")
    public ResponseEntity deleteAllTransactions(){
        transactionService.deleteAllTransactions();
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }
}
