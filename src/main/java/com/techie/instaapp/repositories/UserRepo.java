package com.techie.instaapp.repositories;

import com.techie.instaapp.models.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepo extends MongoRepository<User, String> {
    Optional<User> findByAccountNumber(String accountNumber);
}
