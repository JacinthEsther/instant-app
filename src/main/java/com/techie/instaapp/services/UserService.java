package com.techie.instaapp.services;

import com.techie.instaapp.dtos.requests.BankDeposit;
import com.techie.instaapp.dtos.requests.BankTransfer;
import com.techie.instaapp.dtos.requests.UserRequest;
import com.techie.instaapp.dtos.responses.DepositResponse;
import com.techie.instaapp.dtos.responses.UserResponse;

import java.util.List;

public interface UserService {
    UserResponse createInstantAccount(UserRequest request);


    DepositResponse depositTransactionToOwnersAccount(BankDeposit request, String userId);

    List<DepositResponse> makeTransfer(String userId, BankTransfer transfer);
}
