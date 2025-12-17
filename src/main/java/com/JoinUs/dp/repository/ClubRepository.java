package com.JoinUs.dp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.JoinUs.dp.entity.Club;

@Repository
public interface ClubRepository extends JpaRepository<Club, String> {

    List<Club> findByType(String type);

    List<Club> findByTypeAndCategory(String type, String category);

    List<Club> findByTypeAndDepartment(String type, String department);

    List<Club> findByDepartment(String department);

    List<Club> findByCategory(String category);

    List<Club> findByDepartmentStartingWithIgnoreCase(String department);
}
