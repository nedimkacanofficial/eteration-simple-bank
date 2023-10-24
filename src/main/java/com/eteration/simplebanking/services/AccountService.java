package com.eteration.simplebanking.services;

import com.eteration.simplebanking.dto.AccountSaveDTO;
import com.eteration.simplebanking.mapper.AccountMapper;
import com.eteration.simplebanking.model.Account;
import com.eteration.simplebanking.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// This class is a place holder you can change the complete implementation
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AccountService {
    private final AccountRepository accountRepository;

    /**
     * Method used to find an account with the specified account number.
     *
     * @param accountNumber The account number of the account to be found.
     * @return An object representing the account with the given account number or null (if the account is not found).
     */
    @Transactional(readOnly = true)
    public Account findAccount(String accountNumber) {
        log.info("Request to findAccount() accountNumber: {}", accountNumber);
        return this.accountRepository.findByAccountNumber(accountNumber);
    }

    /**
     * Method used to create a new account.
     *
     * @param account An object representing the account to be created.
     * @return A DTO (Data Transfer Object) containing the information of the created account.
     */
    public AccountSaveDTO createAccount(Account account) {
        log.info("Request to createAccount() createAccount: {}", account);
        return AccountMapper.toDTO(this.accountRepository.save(account));
    }
}
