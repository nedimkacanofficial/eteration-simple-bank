package com.eteration.simplebanking.services;

import com.eteration.simplebanking.controller.TransactionStatus;
import com.eteration.simplebanking.exception.InsufficientBalanceException;
import com.eteration.simplebanking.model.Account;
import com.eteration.simplebanking.model.Transaction;
import com.eteration.simplebanking.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TransactionService {
    private final AccountService accountService;
    private final TransactionRepository transactionRepository;

    /**
     * Saves a transaction associated with a specific account.
     *
     * @param accountNumber The account number to which the transaction is linked.
     * @param transaction   The transaction object representing the financial transaction.
     * @return A TransactionStatus object indicating the result of the transaction, including an approval code.
     * @throws InsufficientBalanceException if the account has insufficient balance to complete the transaction.
     */
    public TransactionStatus saveTransaction(String accountNumber, Transaction transaction) throws InsufficientBalanceException {
        log.info("Request to saveTransaction() accountNumber: {} and Transaction: {}", accountNumber, transaction.getClass().getSimpleName());
        Account account = this.accountService.findAccount(accountNumber);
        if (account != null && transaction.getAmount() > 0) {
            transaction.setDate(LocalDateTime.now());
            transaction.setType(transaction.getClass().getSimpleName());
            transaction.setApprovalCode(UUID.randomUUID().toString());
            account.post(transaction);
            this.transactionRepository.save(transaction);
            return new TransactionStatus(transaction.getApprovalCode());
        }
        throw new InsufficientBalanceException("Insufficient balance");
    }
}
