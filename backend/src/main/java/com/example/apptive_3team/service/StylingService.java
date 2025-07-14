package com.example.apptive_3team.service;

import com.example.apptive_3team.dto.StylingSuggestionDTO;
import com.example.apptive_3team.dto.StylingSuggestionResponseDTO;
import com.example.apptive_3team.entity.MoodReport;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StylingService {

    public StylingSuggestionResponseDTO StylingSuggestion(List<MoodReport> moodReports, Double temp) {

        // 추천할 코디가 하나도 없는 경우
        if (moodReports == null || moodReports.isEmpty()) {
            return new StylingSuggestionResponseDTO(MESSAGE_NO_CODI, 0, Collections.emptyList());
        }

        String baseMessage = getStyleRecommendation(temp);

        // 유효한 이미지가 있는 무드리포트만 필터링
        List<StylingSuggestionDTO> validSuggestions = moodReports.stream()
                .filter(report -> report.getImg_top() != null || report.getImg_bottom() != null || report.getImg_etc() != null)
                .map(report -> new StylingSuggestionDTO(
                        report.getId(),
                        report.getWeatherId(),
                        report.getDate(),
                        report.getImg_top(),
                        report.getImg_bottom(),
                        report.getImg_etc(),
                        report.getScore_feel()
                ))
                .sorted(Comparator
                        .comparing(StylingSuggestionDTO::score_feel, Comparator.nullsLast(Comparator.reverseOrder()))
                        .thenComparing(StylingSuggestionDTO::date, Comparator.reverseOrder())
                )
                .collect(Collectors.toList());

        int count = validSuggestions.size();
        List<StylingSuggestionDTO> topSuggestions = validSuggestions.stream().limit(5).toList();

        String message;
        if  (count < 5) {
            message = baseMessage + MESSAGE_INSUFFICIENT_DATA;
        } else {
            message = baseMessage;
        }

        return new StylingSuggestionResponseDTO(message, count, topSuggestions);
    }


    private static final String MESSAGE_NO_CODI = "코디를 채워서 추천을 받아봐요!";
    private static final String MESSAGE_INSUFFICIENT_DATA = "더 추천받고 싶으시다면 코디를 더 등록해보세요!";

    private static final String VERY_HOT = "날씨가 매우 더운 날이에요! 민소매를 중심으로 시원한 코디 어때요?";
    private static final String HOT = "날씨가 더운 날이에요! 반팔과 같은 짧은 의류를 추천해요!";
    private static final String WARM = "너무 짧은 의상은 오늘 날씨에 추울 수 있어요! 얇은 가디건이나 셔츠는 어떨까요?";
    private static final String COOL = "쌀쌀한 날씨가 예상돼요! 얇은 니트나 가디건을 추천해요!";
    private static final String MILD = "아직은 봄,가을 날씨! 긴티에 외투가 필요해 보여요!";
    private static final String CHILLY = "감기 걸리기 쉬운 날이에요! 트렌치코트나 자켓은 어떨까요?";
    private static final String COLD = "초겨울 날씨에요! 코트나 히트텍 착용을 권장합니다!";
    private static final String FREEZING = "날씨가 너무 추워요! 패딩이나 두꺼운 코트가 필요해 보여요!";

    /**
     * 체감온도를 기반으로 의류 추천 메시지를 반환합니다.
     *
     * @param tempFeelsLike 체감온도
     * @return 스타일 추천 메시지
     */
    public String getStyleRecommendation(Double tempFeelsLike) {
        if (tempFeelsLike >= 28) return VERY_HOT;
        else if (tempFeelsLike >= 23) return HOT;
        else if (tempFeelsLike >= 20) return WARM;
        else if (tempFeelsLike >= 17) return COOL;
        else if (tempFeelsLike >= 12) return MILD;
        else if (tempFeelsLike >= 9) return CHILLY;
        else if (tempFeelsLike >= 5) return COLD;
        else return FREEZING;
    }

}
