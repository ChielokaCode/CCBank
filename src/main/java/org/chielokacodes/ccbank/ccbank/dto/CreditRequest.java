package org.chielokacodes.ccbank.ccbank.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreditRequest {
    private String accountNumber;
    private BigDecimal creditAmount;
}
