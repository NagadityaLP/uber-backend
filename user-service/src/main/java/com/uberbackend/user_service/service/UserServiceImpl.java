package com.uberbackend.user_service.service;

import com.uberbackend.user_service.dto.CreateUserRequest;
import com.uberbackend.user_service.dto.UpdateUserRequest;
import com.uberbackend.user_service.dto.UserResponse;
import com.uberbackend.user_service.entity.User;
import com.uberbackend.user_service.exception.UserAlreadyExistsException;
import com.uberbackend.user_service.exception.UserNotFoundException;
import com.uberbackend.user_service.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserResponse createUser(CreateUserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException(
                    "User already exists with email: " + request.getEmail()
            );
        }

        if (userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new UserAlreadyExistsException(
                    "User already exists with phone number: " + request.getPhoneNumber()
            );
        }

        User user = new User();

        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPhoneNumber(request.getPhoneNumber());

        User savedUser = userRepository.save(user);

        return mapToUserResponse(savedUser);
    }

    @Override
    public UserResponse updateUser(Long id,  UpdateUserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));

        if (request.name() != null) {
            user.setName(request.name());
        }

        if (request.email() != null && !request.email().equals(user.getEmail())) {
            if (userRepository.existsByEmail(request.email())) {
                throw new UserAlreadyExistsException(
                        "User already exists with email: " + request.email()
                );
            }

            user.setEmail(request.email());

        }

        if (request.phoneNumber() != null &&
                !request.phoneNumber().equals(user.getPhoneNumber())) {

            if (userRepository.existsByPhoneNumber(request.phoneNumber())) {
                throw new UserAlreadyExistsException(
                        "User already exists with phone number: "
                                + request.phoneNumber()
                );
            }

            user.setPhoneNumber(request.phoneNumber());
        }

        User updatedUser = userRepository.save(user);

        return mapToUserResponse(updatedUser);
    }

    @Override
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found with id: " + id));

        return mapToUserResponse(user);
    }

    @Override
    public void deleteUser(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new UserNotFoundException(
                                "User not found with id: " + id
                        )
                );

        userRepository.delete(user);
    }

    @Override
    public Page<UserResponse> getAllUsers(Pageable pageable) {
        Page<User> users = userRepository.findAll(pageable);
        return users.map(this::mapToUserResponse);
    }

    @Override
    public boolean userExists(Long userId) {
        return userRepository.existsById(userId);
    }

    private UserResponse mapToUserResponse(User user) {

        UserResponse response = new UserResponse();

        response.setId(user.getId());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setPhoneNumber(user.getPhoneNumber());
        response.setCreatedAt(user.getCreatedAt());

        return response;
    }
}
