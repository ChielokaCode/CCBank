package org.chielokacodes.ccbank.ccbank.controller;

import org.chielokacodes.ccbank.ccbank.dto.*;
import org.chielokacodes.ccbank.ccbank.serviceImpl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserServiceImpl userService;

    @Autowired
    public UserController(UserServiceImpl userService) {
        this.userService = userService;
    }

    @PostMapping("/create-account")
    public ResponseEntity<BankResponse> createAccount(@RequestBody UserRequest userRequest){
        BankResponse response = userService.createAccount(userRequest);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/balance-enquiry")
    public ResponseEntity<BankResponse> balanceEnquiry(@RequestBody EnquiryRequest enquiryRequest){
        BankResponse response = userService.balanceEnquiry(enquiryRequest);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/name-enquiry")
    public ResponseEntity<String> nameEnquiry(@RequestBody EnquiryRequest enquiryRequest){
        String response = userService.nameEnquiry(enquiryRequest);
        String finalResponse = "Your Account Name is " + response;
        return new ResponseEntity<>(finalResponse, HttpStatus.OK);
    }

    @PostMapping("/credit-account")
    public ResponseEntity<BankResponse> creditAccount(@RequestBody CreditRequest creditRequest){
        BankResponse response = userService.creditAccount(creditRequest);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/debit-account")
    public ResponseEntity<BankResponse> debitAccount(@RequestBody DebitRequest debitRequest){
        BankResponse response = userService.debitAccount(debitRequest);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/transfer")
    public ResponseEntity<BankResponse> transferFunds(@RequestBody TransferRequest transferRequest){
        BankResponse response = userService.transferFunds(transferRequest);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
