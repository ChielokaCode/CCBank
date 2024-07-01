package org.chielokacodes.ccbank.ccbank.serviceImpl;

import org.chielokacodes.ccbank.ccbank.dto.*;
import org.chielokacodes.ccbank.ccbank.entity.User;
import org.chielokacodes.ccbank.ccbank.exception.UserAlreadyExistException;
import org.chielokacodes.ccbank.ccbank.exception.UserNotFoundException;
import org.chielokacodes.ccbank.ccbank.repository.TransactionRepository;
import org.chielokacodes.ccbank.ccbank.repository.UserRepository;
import org.chielokacodes.ccbank.ccbank.service.UserService;
import org.chielokacodes.ccbank.ccbank.utils.AccountUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final EmailServiceImpl emailService;
    private final TransactionServiceImpl transactionService;


    @Autowired
    public UserServiceImpl(UserRepository userRepository, EmailServiceImpl emailService, TransactionServiceImpl transactionService) {
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.transactionService = transactionService;
    }

    @Override
    public BankResponse createAccount(UserRequest userRequest) {
//        check if user already exist in the database
        if (userRepository.existsByEmail(userRequest.getEmail())){
            throw new UserAlreadyExistException("User already has an account!");
        }
//        create a new user
        User newUser = User.builder()
                .firstName(userRequest.getFirstName())
                .lastName(userRequest.getLastName())
                .otherName(userRequest.getOtherName())
                .phoneNumber(userRequest.getPhoneNumber())
                .alternativePhone(userRequest.getAlternativePhone())
                .email(userRequest.getEmail())
                .gender(userRequest.getGender())
                .address(userRequest.getAddress())
                .stateOfOrigin(userRequest.getStateOfOrigin())
                .accountNumber(AccountUtils.generateAccountNumber())
                .accountBalance(BigDecimal.ZERO)
                .status("ACTIVE")
                .build();
       User savedUser = userRepository.save(newUser);
//       send email to notify user of account creation

       String body = "Your account has been created successfully. Your account number is " +  savedUser.getAccountNumber();
       emailService.sendMail(savedUser.getEmail(), "Account Created", body);

       return BankResponse.builder()
               .responseCode(AccountUtils.ACCOUNT_SUCCESS_CODE)
               .responseMessage(AccountUtils.ACCOUNT_SUCCESS_MESSAGE)
               .accountInfo(AccountInfo.builder()
                       .accountName(savedUser.getFirstName() + " " + savedUser.getOtherName() + " " + savedUser.getLastName())
                       .accountNumber(savedUser.getAccountNumber())
                       .accountBalance(savedUser.getAccountBalance())
                       .build())
               .build();
    }

    @Override
    public BankResponse balanceEnquiry(EnquiryRequest enquiryRequest) {
        Optional<User> verifiedUser = userRepository.findUserByAccountNumber(enquiryRequest.getAccountNumber());

        if(verifiedUser.isPresent()){
            User savedUser = verifiedUser.get();
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_ENQUIRY_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_ENQUIRY_MESSAGE)
                    .accountInfo(AccountInfo.builder()
                            .accountName(savedUser.getFirstName() + " " + savedUser.getOtherName() + " " + savedUser.getLastName())
                            .accountBalance(savedUser.getAccountBalance())
                            .accountNumber(savedUser.getAccountNumber())
                            .build())
                    .build();
        } else {
            throw new UserNotFoundException("Account not Exist");
        }
    }

    @Override
    public String nameEnquiry(EnquiryRequest enquiryRequest) {
        Optional<User> verifiedUser = userRepository.findUserByAccountNumber(enquiryRequest.getAccountNumber());

        if(verifiedUser.isPresent()){
            User savedUser = verifiedUser.get();
            return savedUser.getFirstName() + " " + savedUser.getOtherName() + " " + savedUser.getLastName();
        } else {
//            a generic Exception handler has been applied to RuntimeException
            throw new RuntimeException("Account not Exist");
        }
    }

    @Override
    public BankResponse creditAccount(CreditRequest creditRequest) {
        Optional<User> verifiedUser = userRepository.findUserByAccountNumber(creditRequest.getAccountNumber());

        if(verifiedUser.isPresent()){
            User userToCredit = verifiedUser.get();

            BigDecimal accountBalance = userToCredit.getAccountBalance().add(creditRequest.getCreditAmount());
            BigDecimal previousAccountBalance = userToCredit.getAccountBalance();

            //            Save credit transaction
            TransactionDto transactionDto = TransactionDto.builder()
                    .sourceAccountNumber(userToCredit.getAccountNumber())
                    .transactionType("CREDIT")
                    .amount(creditRequest.getCreditAmount())
                    .previousAccountBalance(previousAccountBalance)
                    .modifiedAccountBalance(accountBalance)
                    .build();

            transactionService.saveTransaction(transactionDto);

            //            send email to notify user of credit
            String body = "Your account has been credited with "+ creditRequest.getCreditAmount() + ". Total amount is " + accountBalance;
            emailService.sendMail(userToCredit.getEmail(), "Credit Successful", body);

//            set new accountBalance and save User
            userToCredit.setAccountBalance(accountBalance);
            userRepository.save(userToCredit);

            return BankResponse.builder()
                    .responseCode(AccountUtils.CREDIT_SUCCESS_CODE)
                    .responseMessage(AccountUtils.CREDIT_SUCCESS_MESSAGE)
                    .accountInfo(AccountInfo.builder()
                            .accountNumber(userToCredit.getAccountNumber())
                            .accountBalance(userToCredit.getAccountBalance())
                            .accountName(userToCredit.getFirstName() + " " + userToCredit.getOtherName() + " " + userToCredit.getLastName())
                            .build())
                    .build();
        } else {
            throw new UserNotFoundException("Account not Exist");
        }
    }

    @Override
    public BankResponse debitAccount(DebitRequest debitRequest) {
        Optional<User> verifiedUser = userRepository.findUserByAccountNumber(debitRequest.getAccountNumber());

        if(verifiedUser.isPresent()){
            User userToDebit = verifiedUser.get();

            String responseMessage = userToDebit.getAccountBalance().compareTo(BigDecimal.ZERO) == 0 ? "Account has Zero Balance" : userToDebit.getAccountBalance().compareTo(debitRequest.getDebitAmount()) < 0 ? "Account has Insufficient Funds" : AccountUtils.DEBIT_SUCCESS_MESSAGE;
            BigDecimal accountBalance = userToDebit.getAccountBalance().compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO : userToDebit.getAccountBalance().compareTo(debitRequest.getDebitAmount()) < 0 ? userToDebit.getAccountBalance() : userToDebit.getAccountBalance().subtract(debitRequest.getDebitAmount());
            BigDecimal previousAccountBalance = userToDebit.getAccountBalance();

            //          Save debit transaction
            TransactionDto transactionDto = TransactionDto.builder()
                    .sourceAccountNumber(userToDebit.getAccountNumber())
                    .transactionType("DEBIT")
                    .amount(debitRequest.getDebitAmount())
                    .previousAccountBalance(previousAccountBalance)
                    .modifiedAccountBalance(accountBalance)
                    .build();

            transactionService.saveTransaction(transactionDto);

//            send email to notify user of debit
            String body = "Debit information: "+ responseMessage + ". Total amount is " + accountBalance;
            emailService.sendMail(userToDebit.getEmail(), "Credit Successful", body);


//            set new accountBalance and save User
            userToDebit.setAccountBalance(accountBalance);
            userRepository.save(userToDebit);

            return BankResponse.builder()
                    .responseCode(AccountUtils.DEBIT_SUCCESS_CODE)
                    .responseMessage(responseMessage)
                    .accountInfo(AccountInfo.builder()
                            .accountNumber(userToDebit.getAccountNumber())
                            .accountBalance(userToDebit.getAccountBalance())
                            .accountName(userToDebit.getFirstName() + " " + userToDebit.getOtherName() + " " + userToDebit.getLastName())
                            .build())
                    .build();
        } else {
            throw new UserNotFoundException("Account not Exist");
        }
    }

    @Override
    public BankResponse transferFunds(TransferRequest transferRequest) {
//        check if both accounts are in database
        Optional<User> beneficiary = userRepository.findUserByAccountNumber(transferRequest.getBeneficiaryAccountNumber());
        Optional<User> userToTransferTo = userRepository.findUserByAccountNumber(transferRequest.getUserToTransferToAccountNumber());
//        if both accounts exist then do operations
        if (beneficiary.isPresent() && userToTransferTo.isPresent()){
            User beneficiary_new = beneficiary.get();
            User userToTransferTo_new = userToTransferTo.get();


//            check if beneficiary account balance is greater than or equal to the amount to be transferred
            boolean amountCheck =  beneficiary_new.getAccountBalance().compareTo(transferRequest.getAmountToTransfer()) > 0 || beneficiary_new.getAccountBalance().compareTo(transferRequest.getAmountToTransfer()) == 0;

//            if amountCheck is true, substract the amount to transfer from the beneficiary account balance, else return his account balance
            BigDecimal amountDebitFromBeneficiary = amountCheck ? beneficiary_new.getAccountBalance().subtract(transferRequest.getAmountToTransfer()) : beneficiary_new.getAccountBalance();

            BigDecimal amountToTransfer =  transferRequest.getAmountToTransfer();
            //            if amountCheck is true, add the amount to transfer to the userToTransferTo account balance, else return his account balance
            BigDecimal amountReceivedByTransferUser = amountCheck ?  userToTransferTo_new.getAccountBalance().add(amountToTransfer) : userToTransferTo_new.getAccountBalance();

            BigDecimal sourcePreviousAccountBalance = beneficiary_new.getAccountBalance();
            BigDecimal destinationPreviousAccountBalance = userToTransferTo_new.getAccountBalance();

            //            Save debit transaction
            TransactionTransferRequest debitTransactionDto = TransactionTransferRequest.builder()
                    .sourceAccountNumber(beneficiary_new.getAccountNumber())
                    .destinationAccountNumber(userToTransferTo_new.getAccountNumber())
                    .transactionType("TRANSFER-DEBIT")
                    .amount(transferRequest.getAmountToTransfer())
                    .previousAccountBalance(sourcePreviousAccountBalance)
                    .modifiedAccountBalance(amountDebitFromBeneficiary)
                    .build();

            //            Save credit transaction
            TransactionTransferRequest creditTransactionDto = TransactionTransferRequest.builder()
                    .sourceAccountNumber(userToTransferTo_new.getAccountNumber())
                    .destinationAccountNumber(beneficiary_new.getAccountNumber())
                    .transactionType("TRANSFER-CREDIT")
                    .amount(transferRequest.getAmountToTransfer())
                    .previousAccountBalance(destinationPreviousAccountBalance)
                    .modifiedAccountBalance(amountReceivedByTransferUser)
                    .build();

            transactionService.saveTransferTransaction(debitTransactionDto);
            transactionService.saveTransferTransaction(creditTransactionDto);

            //            set the new Account Balance and re-save the users back into the database
            beneficiary_new.setAccountBalance(amountDebitFromBeneficiary);
            userToTransferTo_new.setAccountBalance(amountReceivedByTransferUser);
            userRepository.save(beneficiary_new);
            userRepository.save(userToTransferTo_new);

//            send email to (beneficiary) notifying him of the debit operation
            String debitBody = "Debit information: Account Debited Successfully" + ". Total amount is " + amountDebitFromBeneficiary;
            emailService.sendMail(beneficiary_new.getEmail(), "DEBIT ALERT!", debitBody);

            //    send email to (userToTransferTo) notifying him of the credit operation
            String creditBody = "Credit information: Account Credited Successfully" + ". Total amount is " + amountReceivedByTransferUser;
            emailService.sendMail(userToTransferTo_new.getEmail(), "CREDIT ALERT!", creditBody);

//            return bankResponse
            return BankResponse.builder()
                    .responseCode( amountCheck ? AccountUtils.TRANSFER_SUCCESS_CODE : AccountUtils.TRANSFER_ERROR_CODE)
                    .responseMessage(amountCheck ? AccountUtils.TRANSFER_SUCCESS_MESSAGE : AccountUtils.TRANSFER_ERROR_MESSAGE)
                    .accountInfo(null)
                    .build();

        } else {
            throw new UserNotFoundException("Either accounts or Both does not Exist");
        }
    }
}
