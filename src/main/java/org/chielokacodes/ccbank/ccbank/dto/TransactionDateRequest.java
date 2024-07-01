package org.chielokacodes.ccbank.ccbank.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionDateRequest {
    private LocalDate startDate;
    private LocalDate endDate;

}
