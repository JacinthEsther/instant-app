package com.techie.instaapp.repositories;

import com.techie.instaapp.models.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepo extends MongoRepository<User, String> {
}
