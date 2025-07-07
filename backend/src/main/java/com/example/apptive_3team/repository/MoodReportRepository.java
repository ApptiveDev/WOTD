package com.example.apptive_3team.repository;

import com.example.apptive_3team.entity.MoodReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MoodReportRepository extends JpaRepository<MoodReport, Long> {
    List<MoodReport> findByUserId(Long userId);
    Optional<MoodReport> findByWeatherIdAndUserId(Long weatherId, Long userId);
}
