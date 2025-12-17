// src/main/java/com/JoinUs/dp/repository/ApplicationRepository.java
package com.JoinUs.dp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.JoinUs.dp.entity.Application;
import com.JoinUs.dp.entity.Club;

import java.util.List;

public interface ApplicationRepository extends JpaRepository<Application, Long> {

    List<Application> findByUserId(Long userId);

    List<Application> findByClubId(String clubId);
//    List<Club> findByDepartment(String department);

}
