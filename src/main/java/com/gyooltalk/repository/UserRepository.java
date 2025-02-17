package com.gyooltalk.repository;

import com.gyooltalk.entity.User;
import com.gyooltalk.payload.UserDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    UserDto findByUserEmail(String email);
    boolean existsByUserId(String userId);

}
