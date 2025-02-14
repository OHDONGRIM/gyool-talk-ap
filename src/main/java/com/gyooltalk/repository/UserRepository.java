package com.gyooltalk.repository;

import com.gyooltalk.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUserEmail(String email);

    boolean existsByUserId(String userId);

    User save(User user);
}
