package com.example.apptive_3team.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

// 챙길물품 요청/응답 DTO
public record ItemRequestDTO(Long id,
                             String name,
                             @NotNull LocalDate deadline) {
}