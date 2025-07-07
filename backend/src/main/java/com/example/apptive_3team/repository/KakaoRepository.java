package com.example.apptive_3team.repository;

import com.example.apptive_3team.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface KakaoRepository extends JpaRepository<User, Long> {
    Optional<User> findByProviderId(String providerId);
}
