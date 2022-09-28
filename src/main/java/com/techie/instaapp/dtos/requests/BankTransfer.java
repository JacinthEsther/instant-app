package com.techie.instaapp.dtos.requests;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BankTransfer {

    private String receiverAccountNumber;
    private String senderAccountNumber;
    private double transferAmount;
    private String pin;
}
