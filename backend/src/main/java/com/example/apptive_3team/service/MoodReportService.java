package com.example.apptive_3team.service;

import com.example.apptive_3team.dto.MoodReportRequestDTO;
import com.example.apptive_3team.entity.MoodReport;
import com.example.apptive_3team.exception.MoodReport.MoodReportNotFoundException;
import com.example.apptive_3team.repository.MoodReportRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MoodReportService {

    private final MoodReportRepository moodReportRepository;
    private final WeatherApiService weatherApiService;

    /**
     * 무드리포트를 DB에 저장하는 메서드.
     *
     * @param userId 사용자 ID
     * @param moodReportRequestDTO 무드 리포트 요청 DTO
     */
    public void saveMoodReport(Long userId, MoodReportRequestDTO moodReportRequestDTO) {

        LocalDate date = moodReportRequestDTO.date();
        double lat = moodReportRequestDTO.latitude();
        double lon = moodReportRequestDTO.longitude();

        // 날짜 기준으로 Weather 조회
        Long weatherId = weatherApiService.getWeatherId(date, lat, lon);

        Optional<MoodReport> existingReport = moodReportRepository.findByWeatherIdAndUserId(weatherId, userId);
        if (existingReport.isPresent()) {
            throw new IllegalStateException("이미 해당 날짜에 등록된 무드 리포트가 있습니다.");
        }

        MoodReport moodReport = new MoodReport();
        moodReport.setWeatherId(weatherId);
        moodReport.setUserId(userId);
        moodReport.setDate(date);
        moodReport.setCreated_at(moodReportRequestDTO.created_at());
        moodReport.setLatitude(lat);
        moodReport.setLongitude(lon);
        moodReport.setImg_top(moodReportRequestDTO.img_top());
        moodReport.setImg_bottom(moodReportRequestDTO.img_bottom());
        moodReport.setImg_etc(moodReportRequestDTO.img_etc());
        moodReport.setContent(moodReportRequestDTO.content());
        moodReport.setScore_feel(moodReportRequestDTO.score_feel());

        moodReportRepository.save(moodReport);
    }

    /**
     * ID에 해당하는 무드리포트 1개를 조회하는 메서드.
     *
     * @param id 무드리포트 ID
     */
    public MoodReport getMoodReport(Long id) {
        return moodReportRepository.findById(id).orElseThrow(MoodReportNotFoundException::new);
    }

    /**
     * 사용자 ID에 해당하는 무드리포트를 모두 조회하는 메서드.
     *
     * @param userId 사용자 ID
     */
    public List<MoodReport> getMoodReportsByUserId(Long userId) {
        return moodReportRepository.findByUserId(userId);
    }

    /**
     * 저장된 무드리포트를 수정하는 메서드.
     *
     * @param userId 사용자 ID
     * @param moodReportRequestDTO 수정할 무드리포트 정보를 가진 DTO
     */
    @Transactional
    public void updateMoodReport(Long userId, MoodReportRequestDTO moodReportRequestDTO) {
        MoodReport moodReport = moodReportRepository.findById(moodReportRequestDTO.id())
                .orElseThrow(MoodReportNotFoundException::new);

        if (!Objects.equals(moodReport.getUserId(), userId)) {
            throw new AccessDeniedException("무드 리포트를 수정할 권한이 없습니다.");
        }

        // 무드 리포트의 날짜를 수정하는 경우에 날짜에 해당하는 weatherId도 수정
        if (moodReportRequestDTO.date() != null) {
            LocalDate date = moodReportRequestDTO.date();
            double lat = moodReportRequestDTO.latitude();
            double lon = moodReportRequestDTO.longitude();

            moodReport.setDate(date);
            moodReport.setWeatherId(weatherApiService.getWeatherId(date, lat, lon));
        }

        moodReport.setCreated_at(moodReportRequestDTO.created_at());
        Optional.ofNullable(moodReportRequestDTO.img_top()).ifPresent(moodReport::setImg_top);
        Optional.ofNullable(moodReportRequestDTO.img_bottom()).ifPresent(moodReport::setImg_bottom);
        Optional.ofNullable(moodReportRequestDTO.img_etc()).ifPresent(moodReport::setImg_etc);
        Optional.ofNullable(moodReportRequestDTO.content()).ifPresent(moodReport::setContent);
        Optional.ofNullable(moodReportRequestDTO.score_feel()).ifPresent(moodReport::setScore_feel);
        Optional.ofNullable(moodReportRequestDTO.latitude()).ifPresent(moodReport::setLatitude);
        Optional.ofNullable(moodReportRequestDTO.longitude()).ifPresent(moodReport::setLongitude);
    }

    /**
     * 저장된 무드리포트를 삭제하는 메서드.
     * 
     * @param userId 사용자 ID
     * @param moodReportId 삭제할 무드리포트 ID
     */
    @Transactional
    public void deleteMoodReport(Long userId, Long moodReportId) {
        MoodReport moodReport = moodReportRepository.findById(moodReportId)
                .orElseThrow(MoodReportNotFoundException::new);

        if (!Objects.equals(moodReport.getUserId(), userId)) {
            throw new AccessDeniedException("무드 리포트를 삭제할 권한이 없습니다.");
        }

        moodReportRepository.delete(moodReport);
    }

    /**
     * 날씨 ID 리스트를 stream하여 사용자 ID값과 동시에 관련있는 무드 리포트를 리스트로 반환하는 메서드.
     *
     * @param weatherIds 날씨 ID 리스트
     * @param userId 사용자 ID
     * @return 조건에 부합하는 무드 리포트들을 리스트로 반환
     */
    public List<MoodReport> getMoodReportsByWeatherIdsAndUserId(List<Long> weatherIds, Long userId) {
        List<MoodReport> result = new ArrayList<>();

        for (Long weatherId : weatherIds) {
            moodReportRepository.findByWeatherIdAndUserId(weatherId, userId)
                    .ifPresent(result::add);
        }

        return result;
    }
}
