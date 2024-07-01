package org.chielokacodes.ccbank.ccbank.serviceImpl;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.extern.slf4j.Slf4j;
import org.chielokacodes.ccbank.ccbank.dto.TransactionDateRequest;
import org.chielokacodes.ccbank.ccbank.entity.Transaction;
import org.chielokacodes.ccbank.ccbank.entity.User;
import org.chielokacodes.ccbank.ccbank.exception.TransactionNotFoundException;
import org.chielokacodes.ccbank.ccbank.exception.UserNotFoundException;
import org.chielokacodes.ccbank.ccbank.repository.TransactionRepository;
import org.chielokacodes.ccbank.ccbank.repository.UserRepository;
import org.chielokacodes.ccbank.ccbank.service.BankStatementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
@Slf4j
public class BankStatement implements BankStatementService {
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final EmailServiceImpl emailService;

    @Autowired
    public BankStatement(TransactionRepository transactionRepository, UserRepository userRepository, EmailServiceImpl emailService) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
    }


    @Override
    public List<Transaction> getAllTransaction(){
        return transactionRepository.findAll();
    }

    @Override
    public List<Transaction> getAllTransactionByAccountNumber(String accountNumber){
        Optional<User> savedUser = userRepository.findUserByAccountNumber(accountNumber);
        if(savedUser.isPresent()) {
            return transactionRepository.findAllBySourceAccountNumber(accountNumber);
        }else{
            throw new UserNotFoundException("Account not Found!");
        }
    }


    @Override
    public List<Transaction> getTransactionByCreationDate(String accountNumber, TransactionDateRequest dateRequest) throws Exception {

        //    TO Set time together with the Date -SIDE NOTE (NOT USED IN METHOD)
//        LocalDateTime startDateTime = startLocalDate.atStartOfDay();
//        LocalDateTime endDateTime = endLocalDate.atTime(23, 59, 59);

        Optional<User> savedUser = userRepository.findUserByAccountNumber(accountNumber);
        if(savedUser.isPresent()) {
            User user = savedUser.get();
            List<Transaction> transactionList = transactionRepository.findAllBySourceAccountNumberAndCreatedAtBetween(accountNumber, dateRequest.getStartDate(), dateRequest.getEndDate());

//            design statement into pdf
            Rectangle statementSize = new Rectangle(PageSize.A4);
            Document document = new Document(statementSize);
            log.info("setting size of document");

            String FILE = "src/main/resources/static/myDocument.pdf";
            try (
                    OutputStream outputStream = new FileOutputStream(FILE);
            ){
                PdfWriter.getInstance(document, outputStream);
                document.open();

                PdfPTable bankInfoTable = new PdfPTable(1);
                PdfPCell bankName = new PdfPCell(new Phrase("CC Bank"));
                bankName.setBorder(0);
                bankName.setBackgroundColor(BaseColor.BLUE);
                bankName.setPadding(20f);

                PdfPCell bankAddress = new PdfPCell(new Phrase("72, some Address Lagos, Nigeria"));
                bankAddress.setBorder(0);

                bankInfoTable.addCell(bankName);
                bankInfoTable.addCell(bankAddress);

                PdfPTable statementInfo = new PdfPTable(2);
                PdfPCell customerInfo = new PdfPCell(new Phrase("Start Date" + dateRequest.getStartDate()));
                customerInfo.setBorder(0);
                PdfPCell statement = new PdfPCell(new Phrase("STATEMENT OF ACCOUNT"));
                statement.setBorder(0);
                PdfPCell endDate = new PdfPCell(new Phrase("End Date" + dateRequest.getEndDate()));
                endDate.setBorder(0);
                PdfPCell customerName = new PdfPCell(new Phrase("Customer name: " + user.getFirstName() + " " + user.getOtherName() + " " + user.getLastName()));
                customerName.setBorder(0);
                PdfPCell space = new PdfPCell();
                space.setBorder(0);
                PdfPCell customerAddress = new PdfPCell(new Phrase("Customer address: " + user.getAddress()));
                customerAddress.setBorder(0);

                statementInfo.addCell(customerInfo);
                statementInfo.addCell(statement);
                statementInfo.addCell(endDate);
                statementInfo.addCell(customerName);
                statementInfo.addCell(space);
                statementInfo.addCell(customerAddress);

                PdfPTable spaceTable = new PdfPTable(1);
                PdfPCell spaceCell = new PdfPCell();

                spaceCell.setBorder(0);
                spaceTable.addCell(spaceCell);


                PdfPTable transactionTable = new PdfPTable(9);
                PdfPCell transId = new PdfPCell(new Phrase("Trans Id"));
                transId.setBorder(1);
                transId.setBackgroundColor(BaseColor.BLUE);
                transId.setBorderColor(BaseColor.WHITE);

                PdfPCell createdAt = new PdfPCell(new Phrase("Date"));
                createdAt.setBorder(1);
                createdAt.setBackgroundColor(BaseColor.BLUE);
                createdAt.setBorderColor(BaseColor.WHITE);

                PdfPCell sourceAcctNo = new PdfPCell(new Phrase("Source Acct No"));
                sourceAcctNo.setBorder(1);
                sourceAcctNo.setBackgroundColor(BaseColor.BLUE);
                sourceAcctNo.setBorderColor(BaseColor.WHITE);

                PdfPCell destinationAcctNo = new PdfPCell(new Phrase("Destination Acct No"));
                destinationAcctNo.setBorder(1);
                destinationAcctNo.setBackgroundColor(BaseColor.BLUE);
                destinationAcctNo.setBorderColor(BaseColor.WHITE);

                PdfPCell amount = new PdfPCell(new Phrase("Amount"));
                amount.setBorder(1);
                amount.setBackgroundColor(BaseColor.BLUE);
                amount.setBorderColor(BaseColor.WHITE);

                PdfPCell previousAcctBal = new PdfPCell(new Phrase("Previous Acct Bal"));
                previousAcctBal.setBorder(1);
                previousAcctBal.setBackgroundColor(BaseColor.BLUE);
                previousAcctBal.setBorderColor(BaseColor.WHITE);

                PdfPCell modifiedAcctBal = new PdfPCell(new Phrase("Modified Acct Bal"));
                modifiedAcctBal.setBorder(1);
                modifiedAcctBal.setBackgroundColor(BaseColor.BLUE);
                modifiedAcctBal.setBorderColor(BaseColor.WHITE);

                PdfPCell status = new PdfPCell(new Phrase("Status"));
                status.setBorder(1);
                status.setBackgroundColor(BaseColor.BLUE);
                status.setBorderColor(BaseColor.WHITE);

                PdfPCell transactionType = new PdfPCell(new Phrase("Transaction Type"));
                transactionType.setBorder(1);
                transactionType.setBackgroundColor(BaseColor.BLUE);
                transactionType.setBorderColor(BaseColor.WHITE);



                transactionTable.addCell(transId);
                transactionTable.addCell(createdAt);
                transactionTable.addCell(sourceAcctNo);
                transactionTable.addCell(destinationAcctNo);
                transactionTable.addCell(amount);
                transactionTable.addCell(previousAcctBal);
                transactionTable.addCell(modifiedAcctBal);
                transactionTable.addCell(status);
                transactionTable.addCell(transactionType);

                transactionList.forEach(transaction -> {
                    transactionTable.addCell(new Phrase(transaction.getTransactionId()));
                    transactionTable.addCell(new Phrase(transaction.getCreatedAt().toString()));
                    transactionTable.addCell(new Phrase(transaction.getSourceAccountNumber()));
                    transactionTable.addCell(new Phrase(transaction.getDestinationAccountNumber()));
                    transactionTable.addCell(new Phrase(transaction.getAmount().toString()));
                    transactionTable.addCell(new Phrase(transaction.getPreviousAccountBalance().toString()));
                    transactionTable.addCell(new Phrase(transaction.getModifiedAccountBalance().toString()));
                    transactionTable.addCell(new Phrase(transaction.getStatus()));
                    transactionTable.addCell(new Phrase(transaction.getTransactionType()));

                });

                document.add(bankInfoTable);
                document.add(statementInfo);
                document.add(spaceTable);
                document.add(transactionTable);

                document.close();

//              THEN SEND TO USERS EMAIL
                emailService.sendMailWithAttachment(user.getEmail(), "STATEMENT OF ACCOUNT", "This is your statement of account", FILE);

            }catch (Exception e){
                throw new Exception(e.getMessage());
            }

            //////////
            return transactionList;
        }else {
            throw new UserNotFoundException("Account not Found!");
        }
    }

    @Override
    public Transaction getTransactionById(String transId){
        return transactionRepository.findById(transId).orElseThrow(() -> new TransactionNotFoundException("Transaction not Found"));
    }


}
