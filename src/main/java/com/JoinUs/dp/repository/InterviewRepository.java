package com.JoinUs.dp.repository;

import com.JoinUs.dp.entity.Interview;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InterviewRepository extends JpaRepository<Interview, Long> {

    List<Interview> findByApplicationId(Long applicationId);

    List<Interview> findByClubId(String clubId);

    List<Interview> findByUserId(Long userId);

    boolean existsByApplicationIdAndClubIdAndUserId(Long applicationId, String clubId, Long userId);
}
