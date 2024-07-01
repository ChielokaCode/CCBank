package org.chielokacodes.ccbank.ccbank.serviceImpl;


import lombok.extern.slf4j.Slf4j;
import org.chielokacodes.ccbank.ccbank.dto.TransactionDto;
import org.chielokacodes.ccbank.ccbank.dto.TransactionTransferRequest;
import org.chielokacodes.ccbank.ccbank.entity.Transaction;
import org.chielokacodes.ccbank.ccbank.repository.TransactionRepository;
import org.chielokacodes.ccbank.ccbank.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class TransactionServiceImpl implements TransactionService {
    private final TransactionRepository transactionRepository;

    @Autowired
    public TransactionServiceImpl(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Override
    public void saveTransaction(TransactionDto transactionDto) {
        Transaction transaction = Transaction.builder()
                .transactionType(transactionDto.getTransactionType())
                .amount(transactionDto.getAmount())
                .sourceAccountNumber(transactionDto.getSourceAccountNumber())
                .destinationAccountNumber(null)
                .previousAccountBalance(transactionDto.getPreviousAccountBalance())
                .modifiedAccountBalance(transactionDto.getModifiedAccountBalance())
                .status("SUCCESS")
                .build();
        transactionRepository.save(transaction);
        log.info("Transaction saved Successfully");
    }

//    I created another transfer transaction dto so that I can show the user the From and To account number
    @Override
    public void saveTransferTransaction(TransactionTransferRequest transferTransaction) {
        Transaction transaction = Transaction.builder()
                .transactionType(transferTransaction.getTransactionType())
                .amount(transferTransaction.getAmount())
                .sourceAccountNumber(transferTransaction.getSourceAccountNumber())
                .destinationAccountNumber(transferTransaction.getDestinationAccountNumber())
                .previousAccountBalance(transferTransaction.getPreviousAccountBalance())
                .modifiedAccountBalance(transferTransaction.getModifiedAccountBalance())
                .status("SUCCESS")
                .build();
        transactionRepository.save(transaction);
        log.info("Transfer Transaction saved Successfully");
    }
}
