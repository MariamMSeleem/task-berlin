package com.n26.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class StatisticsResponseDTO {
    private String sum;
    private String avg;
    private String max;
    private String min;
    private long count;
}
