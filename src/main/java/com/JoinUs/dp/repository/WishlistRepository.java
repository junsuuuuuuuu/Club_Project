package com.JoinUs.dp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.JoinUs.dp.entity.Wishlist;

public interface WishlistRepository extends JpaRepository<Wishlist, Long> {

    // 단일 찜 여부 확인
    Optional<Wishlist> findByUserIdAndClubClubId(Long userId, String clubId);

    boolean existsByUserIdAndClubClubId(Long userId, String clubId);

    // 전체 조회 + 타입 필터링 (type이 null이면 전체)
    @Query("""
        SELECT w
        FROM Wishlist w
        JOIN FETCH w.club
        WHERE w.user.id = :userId
          AND (:type IS NULL OR w.club.type = :type)
    """)
    List<Wishlist> findByUserIdAndClubType(
            @Param("userId") Long userId,
            @Param("type") String type);

    // 일반동아리 카테고리별
    @Query("""
        SELECT w
        FROM Wishlist w
        JOIN FETCH w.club
        WHERE w.user.id = :userId
          AND w.club.type = 'general'
          AND w.club.category = :category
    """)
    List<Wishlist> findGeneralByUserIdAndCategory(
            @Param("userId") Long userId,
            @Param("category") String category);

    // 전공동아리 학과별
    @Query("""
        SELECT w
        FROM Wishlist w
        JOIN FETCH w.club
        WHERE w.user.id = :userId
          AND w.club.type = 'major'
          AND w.club.department = :department
    """)
    List<Wishlist> findMajorByUserIdAndDepartment(
            @Param("userId") Long userId,
            @Param("department") String department);
}
