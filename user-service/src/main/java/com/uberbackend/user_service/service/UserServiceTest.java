package com.uberbackend.user_service.service;

import com.uberbackend.user_service.dto.CreateUserRequest;
import com.uberbackend.user_service.dto.UserResponse;
import com.uberbackend.user_service.entity.User;
import com.uberbackend.user_service.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void createUser_shouldCreateUserSuccessfully() {
        // arrange
        CreateUserRequest req = new CreateUserRequest();
        req.setName("John Doe");
        req.setEmail("johndoe@gmail.com");
        req.setPhoneNumber("1234567890");

        User savedUser = new User();
        savedUser.setName("John Doe");
        savedUser.setEmail("johndoe@gmail.com");
        savedUser.setPhoneNumber("1234567890");

        when(userRepository.existsByEmail("johndoe@gmail.com"))
                .thenReturn(false);

        when(userRepository.existsByPhoneNumber("1234567890"))
                .thenReturn(false);

        when(userRepository.save(any(User.class)))
                .thenReturn(savedUser);

        // act
        UserResponse res = userService.createUser(req);

        // assert
        assertNotNull(res);

        assertEquals(1L, res.getId());
        assertEquals("John Doe", res.getName());
        assertEquals("johndoe@gmail.com", res.getEmail());
        assertEquals("1234567890", res.getPhoneNumber());

        // Verify
        verify(userRepository)
                .existsByEmail("johndoe@gmail.com");

        verify(userRepository)
                .existsByPhoneNumber("1234567890");

        verify(userRepository)
                .save(any(User.class));
    }
}
