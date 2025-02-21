package com.gyooltalk.repository;

import com.gyooltalk.entity.User;
import io.lettuce.core.dynamic.annotation.Param;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    User findByUserEmail(String email);
    User findByUserId(String userId);
    boolean existsByUserId(String userId);

    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.userPassword = :password WHERE u.userEmail = :email")
    int resetPassword(@Param("email") String email, @Param("password") String password);


}

