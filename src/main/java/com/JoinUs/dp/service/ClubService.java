package com.JoinUs.dp.service;

import com.JoinUs.dp.common.exception.BadRequestException;
import com.JoinUs.dp.common.exception.NotFoundException;
import com.JoinUs.dp.dto.ClubCreateRequest;
import com.JoinUs.dp.dto.ClubDetailResponse;
import com.JoinUs.dp.dto.ClubListResponse;
import com.JoinUs.dp.dto.ClubListResponse.NoticeSummary;
import com.JoinUs.dp.entity.Club;
import com.JoinUs.dp.entity.ClubImage;
import com.JoinUs.dp.entity.Notice;
import com.JoinUs.dp.repository.ClubImageRepository;
import com.JoinUs.dp.repository.ClubRepository;
import com.JoinUs.dp.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClubService {

    private final ClubRepository clubRepository;
    private final ClubImageRepository imageRepository;
    private final NoticeRepository noticeRepository;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // 1. 동아리 생성
    public Long createClub(ClubCreateRequest req) {

        if (req.getName() == null || req.getShortDescription() == null ||
                req.getType() == null || req.getLeaderId() == null) {
            throw new BadRequestException("name, shortDesc, type, leaderId 값이 필요합니다.");
        }

        Club club = new Club();
        club.setName(req.getName());
        club.setShortDesc(req.getShortDescription());
        club.setDescription(req.getDescription());
        club.setType(req.getType());
        club.setDepartment(req.getDepartment());
        club.setCategory(req.getCategory());
        club.setLeaderId(req.getLeaderId());

        // 기본값
        club.setStatus("pending");
        club.setRecruitStatus("closed");
        club.setRecruiting(false);
        club.setMemberCount(0);
        club.setActivities(null);
        club.setVision(null);
        club.setRecruitmentNotice(null);

        // 추가 입력값 매핑
        Boolean isRecruiting = req.getIsRecruiting();
        if (isRecruiting != null) {
            if (isRecruiting) {
                club.setRecruitStatus("open");
                club.setRecruiting(true);
                if (club.getRecruitmentStartDate() == null) {
                    club.setRecruitmentStartDate(Date.valueOf(LocalDate.now()));
                }
            } else {
                club.setRecruitStatus("closed");
                club.setRecruiting(false);
            }
        }

        if (req.getRecruitDeadline() != null && !req.getRecruitDeadline().isBlank()) {
            LocalDate end = LocalDate.parse(req.getRecruitDeadline(), DATE_FMT);
            club.setRecruitmentEndDate(Date.valueOf(end));
        }

        if (req.getMembers() != null) {
            club.setMemberCount(req.getMembers());
        }

        if (req.getActivities() != null && !req.getActivities().isEmpty()) {
            String activities = String.join("\n", req.getActivities().stream()
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toList()));
            club.setActivities(activities.isEmpty() ? null : activities);
        }

        if (req.getDirection() != null && !req.getDirection().isBlank()) {
            club.setVision(req.getDirection());
        }

        Club saved = clubRepository.save(club);
        return saved.getClubId();
    }

    // 2. 단건 동아리 상세 조회
    public ClubDetailResponse getClubDetail(Long id) {
        Club club = clubRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("해당 clubId가 존재하지 않습니다."));

        var images = imageRepository.findByClub_ClubId(id)
                .stream()
                .map(ClubImage::getImageUrl)
                .collect(Collectors.toList());

        return new ClubDetailResponse(
                club.getClubId(),
                club.getName(),
                club.getShortDesc(),
                club.getDescription(),
                club.getType(),
                club.getDepartment(),
                club.getCategory(),
                club.getRecruitStatus(),
                images
        );
    }

    // 3. 전체 목록 조회 (메인 리스트용)
    public List<ClubListResponse> findAllClubs() {
        return clubRepository.findAll().stream()
                .map(this::toListResponse)
                .collect(Collectors.toList());
    }

    // 4. type별 필터
    public List<ClubListResponse> findByType(String type) {
        return clubRepository.findByType(type).stream()
                .map(this::toListResponse)
                .collect(Collectors.toList());
    }

    // 5. type + category 필터
    public List<ClubListResponse> findByTypeAndCategory(String type, String category) {
        return clubRepository.findByTypeAndCategory(type, category).stream()
                .map(this::toListResponse)
                .collect(Collectors.toList());
    }

    // 6. department 필터
    public List<ClubListResponse> findByDepartment(String department) {
        return clubRepository.findByDepartment(department).stream()
                .map(this::toListResponse)
                .collect(Collectors.toList());
    }

    // 7. 동아리 이미지 업로드
    public Long uploadClubImage(Long clubId, MultipartFile file) {
        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new NotFoundException("해당 clubId가 존재하지 않습니다."));

        if (file == null || file.isEmpty()) {
            throw new BadRequestException("업로드할 파일이 비어 있습니다.");
        }

        try {
            String uploadDir = "uploads/clubs/" + clubId;
            Path dir = Paths.get(uploadDir);
            Files.createDirectories(dir);

            String original = file.getOriginalFilename();
            String ext = "";
            if (original != null && original.contains(".")) {
                ext = original.substring(original.lastIndexOf("."));
            }
            String fileName = UUID.randomUUID() + ext;

            Path target = dir.resolve(fileName);
            file.transferTo(target.toFile());

            String url = "/static/" + uploadDir + "/" + fileName; // 정적 리소스 경로에 맞게 저장

            ClubImage image = new ClubImage();
            image.setClub(club);
            image.setImageUrl(url);
            imageRepository.save(image);

            return image.getImageId();
        } catch (Exception e) {
            throw new RuntimeException("파일 업로드 과정에서 오류가 발생했습니다.", e);
        }
    }

    // 8. 모집 상태 변경 (기존 API - 어드민 전용)
    public String updateRecruitStatus(Long id, String status) {
        Club club = clubRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("해당 clubId가 존재하지 않습니다."));

        if ("open".equals(status)) {
            club.setRecruitmentStartDate(Date.valueOf(LocalDate.now()));
            club.setRecruiting(true);
        } else if ("closed".equals(status)) {
            club.setRecruiting(false);
        } else {
            throw new BadRequestException("status는 open 또는 closed 이어야 합니다.");
        }

        club.setRecruitStatus(status);
        clubRepository.save(club);
        return "updated";
    }

    // 9. 모집 마감일 설정 (기존 API - 어드민 전용)
    public String updateDeadline(Long id, String endDate) {
        Club club = clubRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("해당 clubId가 존재하지 않습니다."));

        club.setRecruitmentEndDate(Date.valueOf(endDate));
        clubRepository.save(club);
        return "deadline updated";
    }

    // 10. 모집 종료 (기존 API - 어드민 전용)
    public String closeRecruit(Long id) {
        Club club = clubRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("해당 clubId가 존재하지 않습니다."));

        club.setRecruitStatus("closed");
        club.setRecruiting(false);
        clubRepository.save(club);

        return "closed";
    }

    // 11. 프론트 요청용 API: 모집 상태 + 마감일 동시 변경
    public void updateRecruitment(Long clubId, Boolean isRecruiting, String recruitDeadline) {
        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new NotFoundException("해당 clubId가 존재하지 않습니다."));

        if (isRecruiting != null) {
            if (isRecruiting) {
                club.setRecruitStatus("open");
                club.setRecruiting(true);
                if (club.getRecruitmentStartDate() == null) {
                    club.setRecruitmentStartDate(Date.valueOf(LocalDate.now()));
                }
            } else {
                club.setRecruitStatus("closed");
                club.setRecruiting(false);
            }
        }

        if (recruitDeadline != null && !recruitDeadline.isBlank()) {
            LocalDate end = LocalDate.parse(recruitDeadline, DATE_FMT);
            club.setRecruitmentEndDate(Date.valueOf(end));
        }

        clubRepository.save(club);
    }

    // =================== 프론트 매핑 메서드 ===================

    private ClubListResponse toListResponse(Club club) {
        // 1) id: "sg01" 같은 형식 (clubId 기반)
        String id = "sg" + String.format("%02d", club.getClubId());

        // 2) adminId: 현재는 임시 고정 값
        //    추후 Users 테이블에서 leaderId 기반 username/email 가져오도록 매핑
        String adminId = "sg_lead";

        // 3) imageUrl: 대표 이미지 1개
        List<ClubImage> images = imageRepository.findByClub_ClubId(club.getClubId());
        String imageUrl = images.isEmpty()
                ? "../../public/assets/smartGrid.png"
                : images.get(0).getImageUrl();

        // 4) activities: Club.activities(TEXT)를 줄바꿈 기준으로 분리
        List<String> activities = club.getActivities() == null
                ? Collections.emptyList()
                : Arrays.stream(club.getActivities().split("\n"))
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .collect(Collectors.toList());

        // 5) direction: Club.vision
        String direction = club.getVision();

        // 6) tags: DB 값에서 파생
        String mainTag = club.getName() == null
                ? null
                : club.getName().replace(" 해커톤,", "").trim();

        boolean hasRoadmap = club.getShortDesc() != null
                && club.getShortDesc().contains("로드맵");

        String energyTag = (club.getCategory() != null && club.getCategory().contains("에너지"))
                ? "에너지"
                : null;

        List<String> tags = new java.util.ArrayList<>();
        if (mainTag != null && !mainTag.isEmpty()) tags.add(mainTag);
        if (energyTag != null) tags.add(energyTag);
        if (hasRoadmap) tags.add("로드맵");

        // 7) recruitDeadline: recruitmentEndDate -> yyyy-MM-dd
        String recruitDeadline = club.getRecruitmentEndDate() == null
                ? null
                : club.getRecruitmentEndDate().toLocalDate().format(DATE_FMT);

        // 8) isRecruiting: recruitStatus or recruiting 둘 중 하나라도 open/true면 open
        boolean isRecruiting = Boolean.TRUE.equals(club.getRecruiting())
                || "open".equalsIgnoreCase(club.getRecruitStatus());

        // 9) members: memberCount
        Integer members = club.getMemberCount();

        // 10) notices: DB Notice 최신순으로 매핑
        List<Notice> notices = noticeRepository.findByClubIdOrderByCreatedAtDesc(club.getClubId());

        List<NoticeSummary> noticeSummaries = notices.stream()
                .map(n -> new NoticeSummary(
                        "sg-notice-" + n.getId(),
                        n.getTitle(),
                        n.getContent(),
                        n.getCreatedAt().toLocalDate().format(DATE_FMT),
                        n.getType() == Notice.Type.CLUB_NOTICE
                ))
                .collect(Collectors.toList());

        // 최종 매핑
        return ClubListResponse.builder()
                .id(id)
                .name(club.getName())
                .type(club.getType())
                .category(club.getCategory())
                .department(club.getDepartment())
                .adminId(adminId)

                .shortDescription(club.getShortDesc())
                .description(club.getDescription())
                .imageUrl(imageUrl)

                .members(members)
                .tags(tags)

                .isRecruiting(isRecruiting)
                .recruitDeadline(recruitDeadline)

                .activities(activities)
                .direction(direction)

                .notices(noticeSummaries)
                .build();
    }
}
