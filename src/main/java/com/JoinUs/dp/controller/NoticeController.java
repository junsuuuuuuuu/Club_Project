package com.JoinUs.dp.controller;

import com.JoinUs.dp.common.response.Response;
import com.JoinUs.dp.entity.Notice;
import com.JoinUs.dp.service.NoticeService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;

    // FAQ 조회
    @GetMapping("/api/notices")
    public ResponseEntity<Response<List<Notice>>> getFaqList() {
        List<Notice> list = noticeService.getFaqList();
        return ResponseEntity.ok(
                new Response<>(HttpStatus.OK.value(), list, "FAQ 조회 성공")
        );
    }

    // 공지 상세 조회
    @GetMapping("/api/notices/{noticeId}")
    public ResponseEntity<Response<Notice>> getNotice(@PathVariable Long noticeId) {
        Notice notice = noticeService.getNotice(noticeId);
        return ResponseEntity.ok(
                new Response<>(HttpStatus.OK.value(), notice, "공지 상세 조회 성공")
        );
    }

    // 특정 동아리 공지 조회
    @GetMapping("/api/clubs/{clubId}/notice")
    public ResponseEntity<Response<List<Notice>>> getClubNotices(@PathVariable String clubId) {
        List<Notice> list = noticeService.getClubNotices(clubId);
        return ResponseEntity.ok(
                new Response<>(HttpStatus.OK.value(), list, "동아리 공지 목록 조회 성공")
        );
    }

    // 클럽 공지 작성
    @PostMapping("/api/clubs/{clubId}/notice")
    public ResponseEntity<Response<Notice>> createClubNotice(
            @PathVariable String clubId,
            @RequestBody Notice req
    ) {
        Notice saved = noticeService.createClubNotice(clubId, req.getTitle(), req.getContent());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new Response<>(HttpStatus.CREATED.value(), saved, "동아리 공지 등록 성공"));
    }

    // FAQ 등록(관리자)
    @PostMapping("/api/notices")
    public ResponseEntity<Response<Notice>> createFaq(
            @RequestBody Notice req
    ) {
        Notice saved = noticeService.createFaq(req.getTitle(), req.getContent());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new Response<>(HttpStatus.CREATED.value(), saved, "FAQ 등록 성공"));
    }
}
