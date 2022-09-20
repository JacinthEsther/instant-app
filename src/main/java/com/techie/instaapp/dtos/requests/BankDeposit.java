package com.techie.instaapp.dtos.requests;

import lombok.Data;

@Data
public class BankDeposit {
    private String accountNumber;
    private double amount;
}
