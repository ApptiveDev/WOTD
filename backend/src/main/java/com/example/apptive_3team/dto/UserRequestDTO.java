package com.example.apptive_3team.dto;

import jakarta.validation.constraints.NotBlank;

// login
public record UserRequestDTO(
        @NotBlank String accessToken,
        Boolean agree
) {
}
