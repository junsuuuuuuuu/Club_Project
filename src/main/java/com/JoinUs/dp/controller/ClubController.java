package com.JoinUs.dp.controller;

import com.JoinUs.dp.dto.ClubCreateRequest;
import com.JoinUs.dp.dto.ClubDetailResponse;
import com.JoinUs.dp.dto.ClubListResponse;
import com.JoinUs.dp.service.ClubService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/clubs")
public class ClubController {

    private final ClubService clubService;

    // =====================================
    // 1. 동아리 생성
    // =====================================
    @PostMapping
    public ResponseEntity<Long> createClub(@RequestBody ClubCreateRequest request) {
        Long id = clubService.createClub(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(id);
    }

    // =====================================
    // 2. 단일 동아리 상세 조회
    // =====================================
    @GetMapping("/{clubId}")
    public ResponseEntity<ClubDetailResponse> getClub(@PathVariable Long clubId) {
        return ResponseEntity.ok(clubService.getClubDetail(clubId));
    }

    // =====================================
    // 3. 동아리 목록 조회 (프론트 요구 구조)
    // =====================================
    @GetMapping
    public ResponseEntity<List<ClubListResponse>> getClubs(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String department
    ) {
        List<ClubListResponse> result;

        if (type != null && category != null) {
            result = clubService.findByTypeAndCategory(type, category);
        } else if (type != null && department != null) {
            result = clubService.findByDepartment(department);
        } else if (type != null) {
            result = clubService.findByType(type);
        } else {
            result = clubService.findAllClubs();
        }

        return ResponseEntity.ok(result);
    }

    // =====================================
    // 4. 이미지 업로드 (multipart)
    // =====================================
    @PostMapping("/{clubId}/image")
    public ResponseEntity<Long> uploadImage(
            @PathVariable Long clubId,
            @RequestParam MultipartFile file
    ) {
        Long imageId = clubService.uploadClubImage(clubId, file);
        return ResponseEntity.ok(imageId);
    }

    // =====================================
    // 5. 모집 상태 + 마감일 통합 변경 API
    // =====================================
    @PatchMapping("/{clubId}/recruitment")
    public ResponseEntity<Void> updateRecruitment(
            @PathVariable Long clubId,
            @RequestBody com.JoinUs.dp.dto.RecruitUpdateRequest request
    ) {
        clubService.updateRecruitment(clubId, request.getIsRecruiting(), request.getRecruitDeadline());
        return ResponseEntity.ok().build();
    }
}
