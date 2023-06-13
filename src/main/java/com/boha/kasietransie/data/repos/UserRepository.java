package com.boha.kasietransie.data.repos;

import com.boha.kasietransie.data.dto.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, String> {
}
