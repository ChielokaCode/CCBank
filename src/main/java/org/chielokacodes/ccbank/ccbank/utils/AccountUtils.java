package org.chielokacodes.ccbank.ccbank.utils;

import jakarta.servlet.http.PushBuilder;
import lombok.Getter;

import java.time.Year;

@Getter
public class AccountUtils {

    public static final String ACCOUNT_EXISTS_CODE = "001";
    public static final String ACCOUNT_EXISTS_MESSAGE = "User already has an account!";

    public static final String ACCOUNT_SUCCESS_CODE = "002";
    public static final String ACCOUNT_SUCCESS_MESSAGE = "Account created Successfully!";

    public static final String ACCOUNT_ENQUIRY_CODE = "003";
    public static final String ACCOUNT_ENQUIRY_MESSAGE = "Account enquiry Successful!";

    public static final String CREDIT_SUCCESS_CODE = "004";
    public static final String CREDIT_SUCCESS_MESSAGE = "Account Successfully Credited!";

    public static final String DEBIT_SUCCESS_CODE = "005";
    public static final String DEBIT_SUCCESS_MESSAGE = "Account Successfully Debited!";

    public static final String TRANSFER_SUCCESS_CODE = "006";
    public static final String TRANSFER_SUCCESS_MESSAGE = "Transfer Successful!";

    public static final String TRANSFER_ERROR_CODE = "007";
    public static final String TRANSFER_ERROR_MESSAGE = "Transfer Unsuccessful! Beneficiary has Insufficient Funds";

    public static String generateAccountNumber() {
        int min = 100000;
        int max = 999999;

        int randomNum = (int) Math.floor(Math.random() * (max - min + 1));

//       Year.now() used to get the current year,
//       so it will get the first four digits of the accountNumber
        return String.valueOf(Year.now()) + randomNum;
    }
}
