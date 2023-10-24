package com.eteration.simplebanking.controller;

import com.eteration.simplebanking.dto.AccountSaveDTO;
import com.eteration.simplebanking.dto.AmountRequestDTO;
import com.eteration.simplebanking.dto.BillPaymentDTO;
import com.eteration.simplebanking.exception.InsufficientBalanceException;
import com.eteration.simplebanking.mapper.AccountMapper;
import com.eteration.simplebanking.model.*;
import com.eteration.simplebanking.repository.TransactionRepository;
import com.eteration.simplebanking.services.AccountService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

// This class is a place holder you can change the complete implementation
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Account Controller")
public class AccountController {
    private final AccountService service;
    private final TransactionRepository transactionRepository;

    /**
     * REST service used to retrieve the information of an account with the specified account number.
     *
     * @param accountNumber The account number of the account to be retrieved.
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
     * REST service used to deposit a specified amount into an account balance.
     *
     * @param accountNumber    The account number into which the deposit will be made.
     * @param amountRequestDTO DTO (Data Transfer Object) used for the deposit transaction.
     * @return ResponseEntity containing the approval code and HTTP status if the operation is successful.
     * If the account is not found or the deposited amount is invalid, in case of an error, it returns an empty ResponseEntity with HttpStatus.BAD_REQUEST.
     */
    @PostMapping("/credit/{accountNumber}")
    public ResponseEntity<TransactionStatus> credit(@PathVariable String accountNumber, @RequestBody AmountRequestDTO amountRequestDTO) throws InsufficientBalanceException {
        log.info("REST to request credit() accountNumber: {} and amountRequestDTO: {}", accountNumber, amountRequestDTO);
        Account account = service.findAccount(accountNumber);
        if (account != null && amountRequestDTO.getAmount() > 0) {
            Transaction transaction = new DepositTransaction(amountRequestDTO.getAmount());
            transaction.setDate(LocalDateTime.now());
            transaction.setType(transaction.getClass().getSimpleName());
            transaction.setApprovalCode(UUID.randomUUID().toString());
            account.post(transaction);
            this.transactionRepository.save(transaction);
            return new ResponseEntity<>(new TransactionStatus(transaction.getApprovalCode()), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    /**
     * REST service used for withdrawing a specified amount of money from an account balance.
     *
     * @param accountNumber    The account number from which the withdrawal will be made.
     * @param amountRequestDTO DTO (Data Transfer Object) used for the withdrawal transaction.
     * @return ResponseEntity containing the approval code and HTTP status if the operation is successful.
     * If the account is not found or the withdrawal amount is invalid, or in case of insufficient balance, an exception is thrown.
     * @throws InsufficientBalanceException Exception thrown in case of insufficient balance.
     */
    @PostMapping("/debit/{accountNumber}")
    public ResponseEntity<TransactionStatus> debit(@PathVariable String accountNumber, @RequestBody AmountRequestDTO amountRequestDTO) throws InsufficientBalanceException {
        log.info("REST to request debit() accountNumber: {} and amountRequestDTO: {}", accountNumber, amountRequestDTO);
        Account account = service.findAccount(accountNumber);
        if (account != null && amountRequestDTO.getAmount() > 0) {
            Transaction transaction = new WithdrawalTransaction(amountRequestDTO.getAmount());
            transaction.setDate(LocalDateTime.now());
            transaction.setType(transaction.getClass().getSimpleName());
            transaction.setApprovalCode(UUID.randomUUID().toString());
            account.post(transaction);
            transactionRepository.save(transaction);
            return new ResponseEntity<>(new TransactionStatus(transaction.getApprovalCode()), HttpStatus.OK);
        } else {
            throw new InsufficientBalanceException("Insufficient balance");
        }
    }

    /**
     * REST service used for bill payment transactions.
     *
     * @param accountNumber  The account number of the account for which the bill payment is to be made.
     * @param billPaymentDTO DTO (Data Transfer Object) used for the bill payment transaction.
     * @return ResponseEntity containing the approval code and HTTP status if the operation is successful.
     * If the account is not found or the payment amount is invalid, or in case of insufficient balance, an exception is thrown.
     * @throws InsufficientBalanceException Exception thrown in case of insufficient balance.
     */
    @PostMapping("/bill/{accountNumber}")
    public ResponseEntity<TransactionStatus> billPayment(@PathVariable String accountNumber, @RequestBody BillPaymentDTO billPaymentDTO) throws InsufficientBalanceException {
        log.info("REST to request billPayment() accountNumber: {} and billPaymentDTO: {}", accountNumber, billPaymentDTO);
        Account account = service.findAccount(accountNumber);
        if (account != null && billPaymentDTO.getAmount() > 0) {
            Transaction transaction = new BillPaymentTransaction(billPaymentDTO.getPayee(), billPaymentDTO.getAmount());
            transaction.setDate(LocalDateTime.now());
            transaction.setType(transaction.getClass().getSimpleName());
            transaction.setApprovalCode(UUID.randomUUID().toString());
            account.post(transaction);
            transactionRepository.save(transaction);
            return new ResponseEntity<>(new TransactionStatus(transaction.getApprovalCode()), HttpStatus.OK);
        } else {
            throw new InsufficientBalanceException("Insufficient balance");
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