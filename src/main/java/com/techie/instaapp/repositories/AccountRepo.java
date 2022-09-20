package com.techie.instaapp.repositories;

import com.techie.instaapp.models.Account;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface AccountRepo extends MongoRepository<Account, String> {

   Optional<Account> findByAccountNumber(String accountNumber);
}
