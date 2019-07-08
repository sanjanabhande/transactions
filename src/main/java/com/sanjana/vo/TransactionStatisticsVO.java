package com.sanjana.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransactionStatisticsVO {

    @JsonIgnore
    private BigDecimal sum;
    @JsonIgnore
    private BigDecimal avg;
    @JsonIgnore
    private BigDecimal max;
    @JsonIgnore
    private BigDecimal min;

    @JsonProperty("sum")
    private String strSum;
    @JsonProperty("avg")
    private String strAvg;
    @JsonProperty("max")
    private String strMax;
    @JsonProperty("min")
    private String strMin;

    private long count;

}
