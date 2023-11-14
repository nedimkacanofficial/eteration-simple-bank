package com.eteration.simplebanking.controller;

import com.eteration.simplebanking.dto.AccountSaveDTO;
import com.eteration.simplebanking.dto.AmountRequestDTO;
import com.eteration.simplebanking.dto.BillPaymentDTO;
import com.eteration.simplebanking.exception.InsufficientBalanceException;
import com.eteration.simplebanking.mapper.AccountMapper;
import com.eteration.simplebanking.model.*;
import com.eteration.simplebanking.services.AccountService;
import com.eteration.simplebanking.services.TransactionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// This class is a place holder you can change the complete implementation
@RestController
@RequestMapping("/account/v1")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Account Controller")
public class AccountController {
    private final AccountService service;
    private final TransactionService transactionService;

    /**
     * REST service used to retrieve the information of an account with the specified account number.
     *
     * @return If the account is found, it returns an ResponseEntity with the account information and HTTP status HttpStatus.OK.
     * If the account is not found, it returns an empty ResponseEntity with HttpStatus.NOT_FOUND.
     */
    @GetMapping("/{accountNumber}")
    public ResponseEntity<Account> getAccount(@PathVariable String accountNumber) {
        log.info("REST to request getAccount() accountNumber: {}", accountNumber);
        Account account = this.service.findAccount(accountNumber);
        if (account == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(account, HttpStatus.OK);
    }

    /**
     * Endpoint to credit funds to a specified account.
     *
     * @param accountNumber    The account number to which funds will be credited.
     * @param amountRequestDTO The request data containing the amount to be credited.
     * @return A ResponseEntity containing the TransactionStatus representing the result of the transaction.
     */
    @PostMapping("/credit/{accountNumber}")
    public ResponseEntity<TransactionStatus> credit(@PathVariable String accountNumber, @RequestBody AmountRequestDTO amountRequestDTO) {
        log.info("REST to request credit() accountNumber: {} and amountRequestDTO: {}", accountNumber, amountRequestDTO);
        try {
            TransactionStatus transactionStatus = this.transactionService.saveTransaction(accountNumber, new DepositTransaction(amountRequestDTO.getAmount()));
            return new ResponseEntity<>(transactionStatus, HttpStatus.OK);
        } catch (InsufficientBalanceException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Endpoint to debit funds from a specified account.
     *
     * @param accountNumber    The account number from which funds will be debited.
     * @param amountRequestDTO The request data containing the amount to be debited.
     * @return A ResponseEntity containing the TransactionStatus representing the result of the transaction.
     */
    @PostMapping("/debit/{accountNumber}")
    public ResponseEntity<TransactionStatus> debit(@PathVariable String accountNumber, @RequestBody AmountRequestDTO amountRequestDTO) {
        log.info("REST to request debit() accountNumber: {} and amountRequestDTO: {}", accountNumber, amountRequestDTO);
        try {
            TransactionStatus transactionStatus = this.transactionService.saveTransaction(accountNumber, new WithdrawalTransaction(amountRequestDTO.getAmount()));
            return new ResponseEntity<>(transactionStatus, HttpStatus.OK);
        } catch (InsufficientBalanceException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Endpoint for making a bill payment from a specified account.
     *
     * @param accountNumber  The account number from which the bill payment will be made.
     * @param billPaymentDTO The request data containing the payee information and the bill payment amount.
     * @return A ResponseEntity containing the TransactionStatus representing the result of the bill payment transaction.
     */
    @PostMapping("/bill/{accountNumber}")
    public ResponseEntity<TransactionStatus> billPayment(@PathVariable String accountNumber, @RequestBody BillPaymentDTO billPaymentDTO) {
        log.info("REST to request billPayment() accountNumber: {} and billPaymentDTO: {}", accountNumber, billPaymentDTO);
        try {
            TransactionStatus transactionStatus = this.transactionService.saveTransaction(accountNumber, new BillPaymentTransaction(billPaymentDTO.getPayee(), billPaymentDTO.getAmount()));
            return new ResponseEntity<>(transactionStatus, HttpStatus.OK);
        } catch (InsufficientBalanceException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * REST service used to create a new account or update an existing account.
     *
     * @param accountSaveDTO DTO (Data Transfer Object) containing the data of the account to be created or updated.
     * @return ResponseEntity containing the information of the created or updated account if the operation is successful.
     * If the account already exists or the operation fails, it returns an empty ResponseEntity with HttpStatus.BAD_REQUEST.
     */
    @PostMapping
    public ResponseEntity<AccountSaveDTO> createAccount(@RequestBody AccountSaveDTO accountSaveDTO) {
        log.info("REST to request createAccount() accountSaveDTO: {}", accountSaveDTO);
        Account account = this.service.findAccount(accountSaveDTO.getAccountNumber());
        if (account == null) {
            AccountSaveDTO response = this.service.createAccount(AccountMapper.toEntity(accountSaveDTO));
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
}