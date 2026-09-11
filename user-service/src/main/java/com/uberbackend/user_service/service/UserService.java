package com.uberbackend.user_service.service;

import com.uberbackend.user_service.dto.CreateUserRequest;
import com.uberbackend.user_service.dto.UpdateUserRequest;
import com.uberbackend.user_service.dto.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
    UserResponse createUser(CreateUserRequest user);
    UserResponse updateUser(Long id, UpdateUserRequest user);
    UserResponse getUserById(Long id);
    void deleteUser(Long id);
    Page<UserResponse> getAllUsers(Pageable pageable);
}
