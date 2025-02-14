package com.gyooltalk.repository;

import com.gyooltalk.entity.EmailAuth;
import org.springframework.data.keyvalue.repository.KeyValueRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface EmailAuthRepository extends KeyValueRepository<EmailAuth, String> {
    Optional<EmailAuth> findByEmail(String email);
}
