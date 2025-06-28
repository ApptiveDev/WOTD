package com.example.apptive_3team.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * 챙길물품의 정보를 전달할 DTO
 *
 * @param name 물품 이름 (50자 제한)
 * @param deadline 물품이 필요한 날짜 (MM-DD)
 */
public record ItemDTO(Long id,

                      @NotBlank
                      @Size(max = 50, message = "가능한 글자수 50자를 넘으셨습니다.")
                      String name,

                      LocalDate deadline) {
}
