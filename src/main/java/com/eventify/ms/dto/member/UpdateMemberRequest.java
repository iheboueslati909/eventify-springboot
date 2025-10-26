package com.eventify.ms.dto.member;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;

public record UpdateMemberRequest(
    @NotBlank(message = "First name is required")
    String firstName,

    @NotBlank(message = "Last name is required")
    String lastName,

    @NotBlank(message = "Email is required")
    @Email(message = "Must be a valid email address")
    String email
) {}