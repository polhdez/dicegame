package com.polhdez.dicegame.repository;

import com.polhdez.dicegame.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, String> {
    User findByUsername(String username);
    User findByToken(String token);
}