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
public class TransactionTransferRequest {
    private String transactionType;
    private BigDecimal amount;
    private String sourceAccountNumber;
    private String destinationAccountNumber;
    private BigDecimal previousAccountBalance;
    private BigDecimal modifiedAccountBalance;
    private String status;
}
