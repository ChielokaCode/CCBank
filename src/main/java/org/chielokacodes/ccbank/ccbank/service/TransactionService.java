package org.chielokacodes.ccbank.ccbank.service;

import org.chielokacodes.ccbank.ccbank.dto.TransactionDto;
import org.chielokacodes.ccbank.ccbank.dto.TransactionTransferRequest;

public interface TransactionService {
    void saveTransaction(TransactionDto transaction);
    void saveTransferTransaction(TransactionTransferRequest transferTransaction);
}
