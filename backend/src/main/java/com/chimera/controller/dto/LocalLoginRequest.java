package com.chimera.controller.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Request body for local email/password authentication.
 */
public record LocalLoginRequest(
        @NotBlank @Email String email,
        @NotBlank String password
) {}
