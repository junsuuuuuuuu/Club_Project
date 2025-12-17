package com.JoinUs.dp.repository;

import com.JoinUs.dp.entity.ClubImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface ClubImageRepository extends JpaRepository<ClubImage, Long> {
    List<ClubImage> findByClub_ClubId(String clubId);

    List<ClubImage> findByClub_ClubIdIn(Collection<String> clubIds);

    void deleteByClub_ClubId(String clubId);
}