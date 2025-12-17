package com.JoinUs.dp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.JoinUs.dp.entity.Club;

@Repository
public interface ClubRepository extends JpaRepository<Club, String> {

    // type(major/general)으로 동아리 필터링
    List<Club> findByType(String type);

    // 일반동아리 category 검색 (ClubService의 findByTypeAndCategory를 지원)
    List<Club> findByTypeAndCategory(String type, String category);

    // 전공동아리 department 검색 (ClubService의 findByTypeAndDepartment를 지원)
    // 참고: ClubService에서는 이 메서드를 사용하지 않고 findByDepartment를 사용하고 있음.
    List<Club> findByTypeAndDepartment(String type, String department);

    // 💡 추가됨: ClubService의 findByDepartment(String department)를 지원
    List<Club> findByDepartment(String department);
}
