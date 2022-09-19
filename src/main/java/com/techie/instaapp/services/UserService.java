package com.techie.instaapp.services;

import com.techie.instaapp.dtos.requests.UserRequest;
import com.techie.instaapp.dtos.responses.BvnResponse;
import com.techie.instaapp.dtos.responses.UserResponse;

import java.math.BigDecimal;

public interface UserService {
    UserResponse createInstantAccount(UserRequest request);

    void depositTransaction(String accountNumber, BigDecimal amount);
}
