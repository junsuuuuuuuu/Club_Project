package com.JoinUs.dp.service;

import com.JoinUs.dp.dto.InterviewRequest;
import com.JoinUs.dp.dto.InterviewResponse;
import com.JoinUs.dp.entity.Interview;
import com.JoinUs.dp.entity.InterviewStatus;
import com.JoinUs.dp.repository.InterviewRepository;
import com.JoinUs.dp.repository.ApplicationRepository;
import com.JoinUs.dp.repository.UserRepository;
import com.JoinUs.dp.repository.ClubRepository;
import com.JoinUs.dp.common.exception.NotFoundException;
import com.JoinUs.dp.common.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InterviewService {

    private final InterviewRepository interviewRepository;
    private final ApplicationRepository applicationRepository;
    private final UserRepository userRepository;
    private final ClubRepository clubRepository;

    /** 면접 생성 */
    public InterviewResponse create(InterviewRequest req) {
        validateRequest(req);
        if (interviewRepository.existsByApplicationIdAndClubIdAndUserId(
                req.getApplicationId(), req.getClubId(), req.getUserId())) {
            throw new BadRequestException("이미 동일 지원/클럽/사용자 조합의 면접이 존재합니다.");
        }
        Interview i = new Interview();
        i.setApplicationId(req.getApplicationId());
        i.setClubId(req.getClubId());
        i.setUserId(req.getUserId());
        i.setSchedule(req.getSchedule());
        i.setLocation(req.getLocation());
        i.setMemo(req.getMemo());

        interviewRepository.save(i);
        return InterviewResponse.from(i);
    }

    /** 면접 수정 */
    public InterviewResponse update(Long id, InterviewRequest req) {
        Interview i = interviewRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Interview not found"));

        if (req.getSchedule() != null) i.setSchedule(req.getSchedule());
        if (req.getLocation() != null) i.setLocation(req.getLocation());
        if (req.getMemo() != null) i.setMemo(req.getMemo());

        interviewRepository.save(i);
        return InterviewResponse.from(i);
    }

    /** 삭제 */
    public void delete(Long id) {
        interviewRepository.deleteById(id);
    }

    /** 신청별 조회 */
    public List<InterviewResponse> getByApplication(Long applicationId) {
        return interviewRepository.findByApplicationId(applicationId)
                .stream().map(InterviewResponse::from).toList();
    }

    /** 동아리별 조회 */
    public List<InterviewResponse> getByClub(String clubId) {
        return interviewRepository.findByClubId(clubId)
                .stream().map(InterviewResponse::from).toList();
    }

    /** 사용자별 조회 */
    public List<InterviewResponse> getByUser(Long userId) {
        return interviewRepository.findByUserId(userId)
                .stream().map(InterviewResponse::from).toList();
    }

    /** 상태 변경 */
    public InterviewResponse changeStatus(Long id, String status) {

        Interview i = interviewRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Interview not found"));

        i.setStatus(InterviewStatus.valueOf(status.toUpperCase()));
        interviewRepository.save(i);

        return InterviewResponse.from(i);
    }

    private void validateRequest(InterviewRequest req) {
        if (req.getApplicationId() == null || !applicationRepository.existsById(req.getApplicationId())) {
            throw new NotFoundException("Application not found");
        }
        if (req.getUserId() == null || !userRepository.existsById(req.getUserId())) {
            throw new NotFoundException("User not found");
        }
        if (req.getClubId() == null || !clubRepository.existsById(req.getClubId())) {
            throw new NotFoundException("Club not found");
        }
    }

}
