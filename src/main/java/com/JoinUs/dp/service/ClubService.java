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
import org.springframework.transaction.annotation.Transactional;

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

    // 1. Club creation
    public String createClub(ClubCreateRequest req) {
        if (req.getName() == null || req.getShortDescription() == null ||
                req.getType() == null || req.getLeaderId() == null) {
            throw new BadRequestException("name, shortDesc, type, leaderId are required.");
        }

        String clubId = normalizeClubId(req.getId());

        Club club = new Club();
        club.setClubId(clubId);
        club.setName(req.getName());
        club.setShortDesc(req.getShortDescription());
        club.setDescription(req.getDescription());
        club.setType(req.getType());
        club.setDepartment(req.getDepartment());
        club.setCategory(req.getCategory());
        club.setLeaderId(req.getLeaderId());

        // defaults
        club.setStatus("pending");
        club.setRecruitStatus("closed");
        club.setRecruiting(false);
        club.setMemberCount(0);
        club.setActivities(null);
        club.setVision(null);
        club.setRecruitmentNotice(null);

        // recruiting flag
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
        return formatClubId(saved.getClubId());
    }

    // 2. Club detail
    public ClubDetailResponse getClubDetail(String id) {
        String normalizedId = normalizeClubId(id);
        Club club = clubRepository.findById(normalizedId)
                .orElseThrow(() -> new NotFoundException("clubId not found."));

        var images = imageRepository.findByClub_ClubId(normalizedId)
                .stream()
                .map(ClubImage::getImageUrl)
                .collect(Collectors.toList());

        return new ClubDetailResponse(
                formatClubId(club.getClubId()),
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

    // 3. Club list
    public List<ClubListResponse> findAllClubs() {
        return clubRepository.findAll().stream()
                .map(this::toListResponse)
                .collect(Collectors.toList());
    }

    // 4. filter by type
    public List<ClubListResponse> findByType(String type) {
        return clubRepository.findByType(type).stream()
                .map(this::toListResponse)
                .collect(Collectors.toList());
    }

    // 4-1. filter by category only
    public List<ClubListResponse> findByCategory(String category) {
        return clubRepository.findByCategory(category).stream()
                .map(this::toListResponse)
                .collect(Collectors.toList());
    }

    // 5. filter by type + category
    public List<ClubListResponse> findByTypeAndCategory(String type, String category) {
        return clubRepository.findByTypeAndCategory(type, category).stream()
                .map(this::toListResponse)
                .collect(Collectors.toList());
    }

    // 6. filter by department
    public List<ClubListResponse> findByDepartment(String department) {
        return clubRepository.findByDepartment(department).stream()
                .map(this::toListResponse)
                .collect(Collectors.toList());
    }

    // 7. upload club image (file upload)
    @Transactional
    public Long uploadClubImage(String clubId, MultipartFile file) {
        String normalizedId = normalizeClubId(clubId);
        Club club = clubRepository.findById(normalizedId)
                .orElseThrow(() -> new NotFoundException("clubId not found."));

        if (file == null || file.isEmpty()) {
            throw new BadRequestException("File is empty.");
        }

        try {
            Path dir = Paths.get("uploads", "clubs", normalizedId);
            Files.createDirectories(dir);

            String original = file.getOriginalFilename();
            String ext = "";
            if (original != null && original.contains(".")) {
                ext = original.substring(original.lastIndexOf("."));
            }
            String fileName = UUID.randomUUID() + ext;

            Path target = dir.resolve(fileName);
            file.transferTo(target.toFile());

            String url = "/static/uploads/clubs/" + normalizedId + "/" + fileName;

            // keep only the latest image
            imageRepository.deleteByClub_ClubId(normalizedId);

            ClubImage image = new ClubImage();
            image.setClub(club);
            image.setImageUrl(url);
            imageRepository.save(image);

            return image.getImageId();
        } catch (Exception e) {
            throw new RuntimeException("Error during file upload.", e);
        }
    }

    // Save image URL only (no file upload)
    @Transactional
    public Long saveClubImageUrl(String clubId, String imageUrl) {
        String normalizedId = normalizeClubId(clubId);
        Club club = clubRepository.findById(normalizedId)
                .orElseThrow(() -> new NotFoundException("clubId not found."));

        if (imageUrl == null || imageUrl.isBlank()) {
            throw new BadRequestException("imageUrl is required.");
        }

        // keep only the latest image
        imageRepository.deleteByClub_ClubId(normalizedId);

        ClubImage image = new ClubImage();
        image.setClub(club);
        image.setImageUrl(imageUrl.trim());
        imageRepository.save(image);
        return image.getImageId();
    }

    // 8. update recruit status
    public String updateRecruitStatus(String id, String status) {
        Club club = clubRepository.findById(normalizeClubId(id))
                .orElseThrow(() -> new NotFoundException("clubId not found."));

        if ("open".equals(status)) {
            club.setRecruitmentStartDate(Date.valueOf(LocalDate.now()));
            club.setRecruiting(true);
        } else if ("closed".equals(status)) {
            club.setRecruiting(false);
        } else {
            throw new BadRequestException("status must be open or closed");
        }

        club.setRecruitStatus(status);
        clubRepository.save(club);
        return "updated";
    }

    // 9. update deadline
    public String updateDeadline(String id, String endDate) {
        Club club = clubRepository.findById(normalizeClubId(id))
                .orElseThrow(() -> new NotFoundException("clubId not found."));

        club.setRecruitmentEndDate(Date.valueOf(endDate));
        clubRepository.save(club);
        return "deadline updated";
    }

    // 10. close recruit
    public String closeRecruit(String id) {
        Club club = clubRepository.findById(normalizeClubId(id))
                .orElseThrow(() -> new NotFoundException("clubId not found."));

        club.setRecruitStatus("closed");
        club.setRecruiting(false);
        clubRepository.save(club);

        return "closed";
    }

    // 11. update recruitment status and deadline
    public void updateRecruitment(String clubId, Boolean isRecruiting, String recruitDeadline) {
        Club club = clubRepository.findById(normalizeClubId(clubId))
                .orElseThrow(() -> new NotFoundException("clubId not found."));

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

    // =================== mappers ===================

    private ClubListResponse toListResponse(Club club) {
        String id = formatClubId(club.getClubId());

        String adminId = "sg_lead";

        List<ClubImage> images = imageRepository.findByClub_ClubId(club.getClubId());
        String imageUrl = images.isEmpty()
                ? "../../public/assets/smartGrid.png"
                : images.get(0).getImageUrl();

        List<String> activities = club.getActivities() == null
                ? Collections.emptyList()
                : Arrays.stream(club.getActivities().split("\n"))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());

        String direction = club.getVision();

        String mainTag = club.getName() == null
                ? null
                : club.getName().trim();

        boolean hasRoadmap = club.getShortDesc() != null
                && club.getShortDesc().toLowerCase().contains("roadmap");

        String energyTag = (club.getCategory() != null && club.getCategory().toLowerCase().contains("energy"))
                ? "energy"
                : null;

        List<String> tags = new java.util.ArrayList<>();
        if (mainTag != null && !mainTag.isEmpty()) tags.add(mainTag);
        if (energyTag != null) tags.add(energyTag);
        if (hasRoadmap) tags.add("roadmap");

        String recruitDeadline = club.getRecruitmentEndDate() == null
                ? null
                : club.getRecruitmentEndDate().toLocalDate().format(DATE_FMT);

        boolean isRecruiting = Boolean.TRUE.equals(club.getRecruiting())
                || "open".equalsIgnoreCase(club.getRecruitStatus());

        Integer members = club.getMemberCount();

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

    private String normalizeClubId(String clubId) {
        if (clubId == null || clubId.isBlank()) {
            throw new BadRequestException("clubId is required.");
        }
        String trimmed = clubId.trim();
        if (trimmed.toLowerCase().startsWith("sg")) {
            trimmed = trimmed.substring(2);
        }
        if (trimmed.isBlank()) {
            throw new BadRequestException("clubId is invalid.");
        }
        return trimmed;
    }

    private String formatClubId(String clubId) {
        if (clubId == null) {
            return null;
        }
        String trimmed = clubId.trim();
        if (trimmed.matches("\\d+")) {
            return "sg" + String.format("%02d", Integer.parseInt(trimmed));
        }
        return trimmed;
    }
}
