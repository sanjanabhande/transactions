package com.sanjana.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
public class TransactionVO {

    private BigDecimal amount;
    private Timestamp timestamp;
}
