package com.JoinUs.dp.repository;

import com.JoinUs.dp.entity.Notice;
import com.JoinUs.dp.entity.Notice.Type;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface NoticeRepository extends JpaRepository<Notice, Long> {

    // FAQ 조회
    List<Notice> findByTypeOrderByCreatedAtDesc(Type type);

    // 동아리 공지 목록 조회
    List<Notice> findByClubIdOrderByCreatedAtDesc(String clubId);

    // 여러 동아리 공지 한 번에 조회
    List<Notice> findByClubIdInOrderByCreatedAtDesc(Collection<String> clubIds);
}
