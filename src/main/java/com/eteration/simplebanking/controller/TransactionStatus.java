package com.eteration.simplebanking.controller;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TransactionStatus {
    private String status;
    private String approvalCode;

    public TransactionStatus(String approvalCode) {
        this.status = "OK";
        this.approvalCode = approvalCode;
    }
}
