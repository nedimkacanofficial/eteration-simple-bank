package com.eteration.simplebanking.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class BillPaymentTransaction extends WithdrawalTransaction {
    private String payee;

    public BillPaymentTransaction(String payee, double amount) {
        super(amount);
        this.payee = payee;
    }

    @Override
    public String toString() {
        return "BillPaymentTransaction{" +
                "payee='" + payee + '\'' +
                '}';
    }
}
