package com.JoinUs.dp.repository;

import com.JoinUs.dp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByStudentId(String studentId);

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    List<User> findByDepartment(String department);

    List<User> findByGrade(int grade);
}
