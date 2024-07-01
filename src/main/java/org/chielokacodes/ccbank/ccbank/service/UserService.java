package org.chielokacodes.ccbank.ccbank.service;

import org.chielokacodes.ccbank.ccbank.dto.*;

public interface UserService {
    BankResponse createAccount(UserRequest userRequest);
    BankResponse balanceEnquiry(EnquiryRequest enquiryRequest);
    String nameEnquiry(EnquiryRequest enquiryRequest);
    BankResponse creditAccount(CreditRequest creditRequest);
    BankResponse debitAccount(DebitRequest creditRequest);
    BankResponse transferFunds(TransferRequest transferRequest);

}
