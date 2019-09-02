package com.n26.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;



@AllArgsConstructor
@Getter
@Setter
public class StatisticsDTO {

    private BigDecimal sum;
    private BigDecimal avg;
    private BigDecimal max;
    private BigDecimal min;
    private long count;

    public StatisticsDTO(){
        sum = new BigDecimal(0);
        max = new BigDecimal(Integer.MIN_VALUE);
        min = new BigDecimal(Integer.MAX_VALUE);
        avg = new BigDecimal(0);
        count = 0L;
    }
}
