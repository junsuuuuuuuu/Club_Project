package com.JoinUs.dp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.JoinUs.dp.entity.ClubSearch;

public interface ClubSearchRepository extends JpaRepository<ClubSearch, String> {

    List<ClubSearch> findByNameContainingIgnoreCase(String name);
    List<ClubSearch> findByNameContainingIgnoreCaseAndMemberCountGreaterThanEqual(String name, Integer memberCount);
    List<ClubSearch> findByNameContainingIgnoreCaseAndRecruiting(String name, Boolean recruiting);
    List<ClubSearch> findByNameContainingIgnoreCaseAndMemberCountGreaterThanEqualAndRecruiting(String name, Integer memberCount, Boolean recruiting);
}
