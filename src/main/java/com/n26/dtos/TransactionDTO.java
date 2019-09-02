package com.n26.dtos;

import lombok.*;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TransactionDTO {
    private String amount;
    private String timestamp;

}
