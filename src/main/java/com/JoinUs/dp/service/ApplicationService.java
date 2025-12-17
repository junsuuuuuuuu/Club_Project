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

    /* 1. 신청 작성 */
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

    /* 3. userId별 조회 (마이페이지용) */
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
        // 상태는 합격/불합격/추가합격 전용 API에서만 변경

        return toDto(repository.save(e));
    }

        /* 7. 부분수정(message만) */
    @Transactional
    public ApplicationDto partialUpdate(Long id, com.JoinUs.dp.dto.ApplicationPartialUpdateRequest updates) {
        Application e = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("지원서를 찾을 수 없습니다. id=" + id));

        if (updates.getMessage() != null) {
            e.setMessage(updates.getMessage());
        }
        // 상태는 setResult/confirm/additionalOffer에서만 변경

        return toDto(repository.save(e));
    }

    /* 8. 동아리별 신청 목록 조회 */
    @Transactional(readOnly = true)
    public List<ApplicationDto> findByClubId(String clubId) {
        if (clubId == null) {
            throw new BadRequestException("clubId가 필요합니다.");
        }
        return repository.findByClubId(clubId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /* 9. 합격/불합격 결정 */
    @Transactional
    public ApplicationDto setResult(Long id, String result, String message) {
        Application e = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("신청을 찾을 수 없습니다. id=" + id));

        if ("passed".equalsIgnoreCase(result)) {
            e.setStatus(ClubStatus.PASSED);   // enum에 있는 값만 사용
        } else if ("failed".equalsIgnoreCase(result)) {
            e.setStatus(ClubStatus.FAILED);
        } else {
            throw new BadRequestException("result 값이 passed 또는 failed 이어야 합니다.");
        }

        e.setMessage(message);
        return toDto(repository.save(e));
    }

    /* 10. 확정/철회 (즉 CONFIRMED / DECLINED) */
    @Transactional
    public ApplicationDto confirmOrDecline(Long id, String action) {
        Application e = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("신청을 찾을 수 없습니다. id=" + id));

        if ("confirm".equalsIgnoreCase(action)) {
            e.setStatus(ClubStatus.CONFIRMED);
        } else if ("decline".equalsIgnoreCase(action)) {
            e.setStatus(ClubStatus.DECLINED);
        } else {
            throw new BadRequestException("action 값이 confirm 또는 decline 이어야 합니다.");
        }
        return toDto(repository.save(e));
    }

    /* 11. 추가 합격 제안(요청이 와도 그냥 PASSED로 처리) */
    @Transactional
    public ApplicationDto additionalOffer(Long id) {
        Application e = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("신청을 찾을 수 없습니다. id=" + id));

        e.setStatus(ClubStatus.PASSED);   // 추가 합격도 PASSED로 통일
        return toDto(repository.save(e));
    }

    /* 12. 학과별 동아리 목록 */
    @Transactional(readOnly = true)
    public List<ClubSummary> getClubsByDepartment(String departmentId) {
        // departmentId가 곧 department로 사용
        List<Club> clubs = clubRepository.findByDepartment(departmentId);
        return clubs.stream()
                .map(c -> new ClubSummary(c.getClubId(), c.getName()))
                .collect(Collectors.toList());
    }

    // ================= 변환 메서드 =================

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
