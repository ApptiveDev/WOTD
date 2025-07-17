package com.example.apptive_3team.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * 챙길물품의 정보를 전달할 DTO
 *
 * @param name 물품 이름 (50자 제한)
 * @param deadline 물품이 필요한 날짜 (MM-DD)
 */
public record ItemRequestDTO(Long id,
                             @NotBlank String name,
                             @NotNull LocalDate deadline) {
}
