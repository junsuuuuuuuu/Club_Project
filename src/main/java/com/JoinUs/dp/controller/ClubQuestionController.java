package com.JoinUs.dp.controller;

import java.util.List;
import java.util.stream.Collectors;

import com.JoinUs.dp.common.exception.NotFoundException;
import com.JoinUs.dp.dto.AnswerUpdateRequest;
import com.JoinUs.dp.dto.QuestionCreateRequest;
import com.JoinUs.dp.entity.ClubQuestion;
import com.JoinUs.dp.entity.ClubSearch;
import com.JoinUs.dp.repository.ClubSearchRepository;
import com.JoinUs.dp.service.ClubQuestionService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/clubs/{clubId}/questions")
@RequiredArgsConstructor
public class ClubQuestionController {

    private final ClubQuestionService clubQuestionService;
    private final ClubSearchRepository clubSearchRepository;

    /** 지원 질문 목록 조회
     *  → 클럽이 설정한 질문 리스트 + (임시) 최대 글자 수
     */
    @GetMapping
    public List<QuestionResponse> getQuestions(@PathVariable String clubId) {
        ClubSearch club = clubSearchRepository.findById(parseClubId(clubId))
                .orElseThrow(() -> new NotFoundException("해당 clubId는 존재하지 않습니다."));

        List<ClubQuestion> questions = clubQuestionService.getQuestionsByClub(club);

        // maxLength는 아직 DB에 없어서 일단 1000자로 고정 (필요하면 엔티티/컬럼 추가해서 변경)
        return questions.stream()
                .map(q -> new QuestionResponse(
                        q.getQid(),
                        q.getQuestion(),
                        1000
                ))
                .collect(Collectors.toList());
    }

    /** 질문 + 답변 목록 조회 (새 엔드포인트) */
    @GetMapping("/answers")
    public List<AnsweredQuestionResponse> getQuestionsWithAnswers(@PathVariable String clubId) {
        ClubSearch club = clubSearchRepository.findById(parseClubId(clubId))
                .orElseThrow(() -> new NotFoundException("해당 clubId는 존재하지 않습니다."));

        return clubQuestionService.getQuestionsByClub(club).stream()
                .map(q -> new AnsweredQuestionResponse(
                        q.getQid(),
                        q.getQuestion(),
                        q.getAnswer()
                ))
                .collect(Collectors.toList());
    }

    /** 질문 추가 (관리자/동아리장용) */
    @PostMapping
    public QuestionResponse addQuestion(
            @PathVariable String clubId,
            @RequestBody QuestionCreateRequest req
    ) {
        ClubSearch club = clubSearchRepository.findById(parseClubId(clubId))
                .orElseThrow(() -> new NotFoundException("해당 clubId는 존재하지 않습니다."));

        ClubQuestion saved = clubQuestionService.addQuestion(club, req.getQuestion());
        return new QuestionResponse(saved.getQid(), saved.getQuestion(), 1000);
    }

    /** 답변 등록/수정 – 지금 구조상 Q&A 성격이라 남겨둠 */
    @PutMapping("/{questionId}/answer")
    public ClubQuestion updateAnswer(
            @PathVariable Long questionId,
            @RequestBody AnswerUpdateRequest req
    ) {
        return clubQuestionService.updateAnswer(questionId, req.getAnswer());
    }

    /** 질문 소프트 삭제 */
    @DeleteMapping("/{questionId}")
    public void softDeleteQuestion(@PathVariable Long questionId) {
        clubQuestionService.softDeleteQuestion(questionId);
    }

    @Data
    @AllArgsConstructor
    private static class QuestionResponse {
        private Long id;
        private String question;
        private int maxLength;
    }

    @Data
    @AllArgsConstructor
    private static class AnsweredQuestionResponse {
        private Long id;
        private String question;
        private String answer;
    }

    private Long parseClubId(String clubId) {
        if (clubId == null) throw new NotFoundException("clubId는 필수입니다.");
        String trimmed = clubId.trim();
        if (trimmed.toLowerCase().startsWith("sg")) {
            trimmed = trimmed.substring(2);
        }
        try {
            return Long.parseLong(trimmed);
        } catch (NumberFormatException e) {
            throw new NotFoundException("clubId 형식이 올바르지 않습니다.");
        }
    }
}
