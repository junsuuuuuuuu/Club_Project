package com.JoinUs.dp.repository;

import com.JoinUs.dp.entity.Notice;
import com.JoinUs.dp.entity.Notice.Type;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NoticeRepository extends JpaRepository<Notice, Long> {

    // FAQ 조회
    List<Notice> findByTypeOrderByCreatedAtDesc(Type type);

    // 특정 클럽 공지 조회
    List<Notice> findByClubIdOrderByCreatedAtDesc(String clubId);

    // 여러 클럽 공지 동시 조회 (N+1 해결용)
    List<Notice> findByClubIdInOrderByCreatedAtDesc(List<String> clubIds);
}
