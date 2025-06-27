package com.example.apptive_3team.dto;

public record UserResponseDTO(
        Long id,
        String name,
        String providerId,
        String providerType,
        String token
) {
}
