package com.example.apptive_3team.dto;

import java.util.List;

public record StylingSuggestionResponseDTO(String message,
                                           int cnt_suggestions,
                                           List<StylingSuggestionDTO> suggestions) {
}
