package com.JoinUs.dp.service;

import com.JoinUs.dp.common.exception.BadRequestException;
import com.JoinUs.dp.common.exception.NotFoundException;
import com.JoinUs.dp.dto.ApplicationDto;
import com.JoinUs.dp.dto.ClubSummary;
import com.JoinUs.dp.entity.Application;
import com.JoinUs.dp.entity.Club;
import com.JoinUs.dp.entity.ClubStatus;
import com.JoinUs.dp.repository.ApplicationRepository;
import com.JoinUs.dp.repository.ClubRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ApplicationService {

    private final ApplicationRepository repository;
    private final ClubRepository clubRepository;

    /* 1. 신청 생성 */
    @Transactional
    public ApplicationDto apply(ApplicationDto dto) {
        Application e = toEntity(dto);
        e.setStatus(ClubStatus.PENDING);
        return toDto(repository.save(e));
    }

    /* 2. 전체 조회 (관리자용) */
    @Transactional(readOnly = true)
    public List<ApplicationDto> findAll() {
        return repository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /* 3. userId로 조회 (마이페이지용) */
    @Transactional(readOnly = true)
    public List<ApplicationDto> findByUserId(Long userId) {
        return repository.findByUserId(userId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /* 4. 단건 조회 */
    @Transactional(readOnly = true)
    public ApplicationDto findById(Long id) {
        Application e = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("신청을 찾을 수 없습니다. id=" + id));
        return toDto(e);
    }

    /* 5. 신청 취소 */
    @Transactional
    public void cancel(Long id) {
        if (!repository.existsById(id)) {
            throw new NotFoundException("신청을 찾을 수 없습니다. id=" + id);
        }
        repository.deleteById(id);
    }

    /* 6. 전체 수정 */
    @Transactional
    public ApplicationDto update(ApplicationDto dto) {
        Application e = repository.findById(dto.getApplicationId())
                .orElseThrow(() -> new NotFoundException("신청을 찾을 수 없습니다. id=" + dto.getApplicationId()));

        e.setMessage(dto.getMessage());
        e.setStatus(dto.getStatus());

        return toDto(repository.save(e));
    }

    /* 7. 부분 수정 (message, status 등) */
    @Transactional
    public ApplicationDto partialUpdate(Long id, Map<String, Object> updates) {
        Application e = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("신청을 찾을 수 없습니다. id=" + id));

        if (updates.containsKey("message")) {
            e.setMessage((String) updates.get("message"));
        }
        if (updates.containsKey("status")) {
            String status = (String) updates.get("status");
            e.setStatus(ClubStatus.valueOf(status));
        }

        return toDto(repository.save(e));
    }

    /* 8. 클럽별 신청 목록 조회 */
    @Transactional(readOnly = true)
    public List<ApplicationDto> findByClubId(Long clubId) {
        return repository.findByClubId(clubId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /* 9. 합격/불합격 설정 */
    @Transactional
    public ApplicationDto setResult(Long id, String result, String message) {
        Application e = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("신청을 찾을 수 없습니다. id=" + id));

        if ("passed".equalsIgnoreCase(result)) {
            e.setStatus(ClubStatus.PASSED);   // ✅ enum에 있는 값만 사용
        } else if ("failed".equalsIgnoreCase(result)) {
            e.setStatus(ClubStatus.FAILED);
        } else {
            throw new BadRequestException("result 값은 passed 또는 failed 이어야 합니다.");
        }

        e.setMessage(message);
        return toDto(repository.save(e));
    }

    /* 10. 확정/철회 (예: CONFIRMED / DECLINED) */
    @Transactional
    public ApplicationDto confirmOrDecline(Long id, String action) {
        Application e = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("신청을 찾을 수 없습니다. id=" + id));

        if ("confirm".equalsIgnoreCase(action)) {
            e.setStatus(ClubStatus.CONFIRMED);
        } else if ("decline".equalsIgnoreCase(action)) {
            e.setStatus(ClubStatus.DECLINED);
        } else {
            throw new BadRequestException("action 값은 confirm 또는 decline 이어야 합니다.");
        }
        return toDto(repository.save(e));
    }

    /* 11. 추가 합격 → 내부적으로도 그냥 PASSED로 처리 */
    @Transactional
    public ApplicationDto additionalOffer(Long id) {
        Application e = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("신청을 찾을 수 없습니다. id=" + id));

        e.setStatus(ClubStatus.PASSED);   // ✅ ADDITIONAL_PASSED 대신 PASSED로 통일
        return toDto(repository.save(e));
    }

    /* 12. 학과별 클럽 목록 */
    @Transactional(readOnly = true)
    public List<ClubSummary> getClubsByDepartment(String departmentId) {
        // departmentId를 그대로 department로 사용
        List<Club> clubs = clubRepository.findByDepartment(departmentId);
        return clubs.stream()
                .map(c -> new ClubSummary(c.getClubId(), c.getName()))
                .collect(Collectors.toList());
    }

    // ================= 내부 매핑 =================

    private ApplicationDto toDto(Application e) {
        return ApplicationDto.from(e);
    }

    private Application toEntity(ApplicationDto dto) {
        Application e = new Application();
        e.setId(dto.getApplicationId());
        e.setUserId(dto.getUserId());
        e.setClubId(dto.getClubId());
        e.setStatus(dto.getStatus());
        e.setMessage(dto.getMessage());
        return e;
    }
}
