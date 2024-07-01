package org.chielokacodes.ccbank.ccbank.controller;

import org.chielokacodes.ccbank.ccbank.dto.TransactionDateRequest;
import org.chielokacodes.ccbank.ccbank.entity.Transaction;
import org.chielokacodes.ccbank.ccbank.serviceImpl.BankStatement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/statement")
public class BankStatementController {

    private final BankStatement bankStatement;


    @Autowired
    public BankStatementController(BankStatement bankStatement) {
        this.bankStatement = bankStatement;
    }

    @GetMapping("/get-all-transactions")
    public ResponseEntity<List<Transaction>> getAllTransaction(){
        List<Transaction> response = bankStatement.getAllTransaction();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/get-all-transactions/{acc}")
    public ResponseEntity<List<Transaction>> getAllTransaction(@PathVariable(name = "acc") String accountNumber){
        List<Transaction> response = bankStatement.getAllTransactionByAccountNumber(accountNumber);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/get-transactions-byDate/{acc}")
    public ResponseEntity<List<Transaction>> getTransactionByCreationDate(@PathVariable(name = "acc") String accountNumber, @RequestBody TransactionDateRequest request) throws Exception {
        List<Transaction> response = bankStatement.getTransactionByCreationDate(accountNumber, request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/get-transactions/{transId}")
    public ResponseEntity<Transaction> getTransactionById(@PathVariable String transId){
        Transaction response = bankStatement.getTransactionById(transId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
