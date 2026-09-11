package com.uberbackend.user_service.repository;

import com.uberbackend.user_service.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(String email);

    boolean existsByPhoneNumber(String phoneNumber);
}
