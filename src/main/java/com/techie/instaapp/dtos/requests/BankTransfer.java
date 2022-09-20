package com.techie.instaapp.dtos.requests;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BankTransfer {

    private String accountNumber;
    private String bankName;
    private double transferAmount;
}
