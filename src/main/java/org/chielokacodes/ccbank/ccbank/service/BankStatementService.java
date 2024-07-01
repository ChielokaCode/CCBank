package org.chielokacodes.ccbank.ccbank.service;

import org.chielokacodes.ccbank.ccbank.dto.TransactionDateRequest;
import org.chielokacodes.ccbank.ccbank.entity.Transaction;

import java.util.List;

public interface BankStatementService {
    List<Transaction> getAllTransaction();
    List<Transaction> getAllTransactionByAccountNumber(String accountNumber);
    List<Transaction> getTransactionByCreationDate(String accountNumber, TransactionDateRequest dateRequest) throws Exception;
    Transaction getTransactionById(String transId);

}
