package com.uberbackend.user_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;

public record UpdateUserRequest(

        String name,

        @Email(message = "Invalid email format")
        String email,

        @Pattern(
                regexp = "^\\+91[0-9]{10}$",
                message = "Phone number must be a valid Indian number with +91"
        )
        String phoneNumber
) {
}
