package com.JoinUs.dp.controller;

import com.JoinUs.dp.common.response.Response;
import com.JoinUs.dp.dto.InterviewRequest;
import com.JoinUs.dp.dto.InterviewResponse;
import com.JoinUs.dp.dto.StatusRequest;
import com.JoinUs.dp.service.InterviewService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class InterviewController {

    private final InterviewService interviewService;

    /** 생성 */
    @PostMapping("/api/interviews")
    public ResponseEntity<Response<InterviewResponse>> create(@RequestBody InterviewRequest req) {
        return ResponseEntity.ok(
                new Response<>(200, interviewService.create(req), "면접 등록 완료")
        );
    }

    /** 수정 */
    @PutMapping("/api/interviews/{id}")
    public ResponseEntity<Response<InterviewResponse>> update(
            @PathVariable Long id,
            @RequestBody InterviewRequest req) {

        return ResponseEntity.ok(
                new Response<>(200, interviewService.update(id, req), "면접 수정 완료")
        );
    }

    /** 삭제 */
    @DeleteMapping("/api/interviews/{id}")
    public ResponseEntity<Response<Void>> delete(@PathVariable Long id) {
        interviewService.delete(id);
        return ResponseEntity.ok(
                new Response<>(200, null, "면접 삭제 완료")
        );
    }

    /** 신청별 조회 */
    @GetMapping("/api/applications/{applicationId}/interviews")
    public ResponseEntity<Response<List<InterviewResponse>>> byApplication(@PathVariable Long applicationId) {
        return ResponseEntity.ok(
                new Response<>(200, interviewService.getByApplication(applicationId), "신청 기준 면접 조회 완료")
        );
    }

    /** 동아리별 조회 */
    @GetMapping("/api/clubs/{clubId}/interviews")
    public ResponseEntity<Response<List<InterviewResponse>>> byClub(@PathVariable String clubId) {
        return ResponseEntity.ok(
                new Response<>(200, interviewService.getByClub(clubId), "동아리 기준 면접 조회 완료")
        );
    }

    /** 사용자별 조회 */
    @GetMapping("/api/users/{userId}/interviews")
    public ResponseEntity<Response<List<InterviewResponse>>> byUser(@PathVariable Long userId) {
        return ResponseEntity.ok(
                new Response<>(200, interviewService.getByUser(userId), "사용자 기준 면접 조회 완료")
        );
    }

    /** 상태 변경 */
    @PatchMapping("/api/interviews/{id}/status")
    public ResponseEntity<Response<InterviewResponse>> changeStatus(
            @PathVariable Long id,
            @RequestBody StatusRequest req
    ) {
        return ResponseEntity.ok(
                new Response<>(200, interviewService.changeStatus(id, req.getStatus()), "면접 상태 변경 완료")
        );
    }

}
