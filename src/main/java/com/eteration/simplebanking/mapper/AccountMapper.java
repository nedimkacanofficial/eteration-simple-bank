package com.eteration.simplebanking.mapper;

import com.eteration.simplebanking.dto.AccountSaveDTO;
import com.eteration.simplebanking.model.Account;

public class AccountMapper {
    public static Account toEntity(AccountSaveDTO accountSaveDTO) {
        Account account = new Account();
        account.setOwner(accountSaveDTO.getOwner());
        account.setAccountNumber(accountSaveDTO.getAccountNumber());
        return account;
    }

    public static AccountSaveDTO toDTO(Account account) {
        AccountSaveDTO accountSaveDTO = new AccountSaveDTO();
        accountSaveDTO.setOwner(account.getOwner());
        accountSaveDTO.setAccountNumber(account.getAccountNumber());
        return accountSaveDTO;
    }
}
