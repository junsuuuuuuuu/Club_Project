package com.JoinUs.dp.service;

import com.JoinUs.dp.entity.Notice;
import com.JoinUs.dp.entity.Notice.Type;
import com.JoinUs.dp.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository noticeRepository;

    // FAQ 전체 조회
    public List<Notice> getFaqList() {
        return noticeRepository.findByTypeOrderByCreatedAtDesc(Type.FAQ);
    }

    // 공지 상세 조회
    public Notice getNotice(Long id) {
        return noticeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notice not found"));
    }

    // 특정 클럽 공지 조회
    public List<Notice> getClubNotices(String clubId) {
        return noticeRepository.findByClubIdOrderByCreatedAtDesc(clubId);
    }

    // 클럽 공지 생성
    public Notice createClubNotice(String clubId, String title, String content) {
        Notice notice = new Notice(clubId, title, content, Type.CLUB_NOTICE);
        return noticeRepository.save(notice);
    }

    // FAQ 생성 (관리자용)
    public Notice createFaq(String title, String content) {
        Notice notice = new Notice(null, title, content, Type.FAQ);
        return noticeRepository.save(notice);
    }
}
