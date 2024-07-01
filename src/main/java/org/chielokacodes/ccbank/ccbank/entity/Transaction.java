package org.chielokacodes.ccbank.ccbank.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "transaction")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String transactionId;
    private String transactionType;
    private BigDecimal amount;
    private String sourceAccountNumber;
    private String destinationAccountNumber;
    private BigDecimal previousAccountBalance;
    private BigDecimal modifiedAccountBalance;
    private String status;
    @CreationTimestamp
    private LocalDate createdAt;
}
