package com.JoinUs.dp.service;

import java.util.List;

import com.JoinUs.dp.common.exception.NotFoundException;
import com.JoinUs.dp.entity.ClubQuestion;
import com.JoinUs.dp.entity.ClubSearch;
import com.JoinUs.dp.repository.ClubQuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClubQuestionService {

    private final ClubQuestionRepository clubQuestionRepository;

    /** 특정 클럽의 활성 질문 목록 */
    public List<ClubQuestion> getQuestionsByClub(ClubSearch club) {
        return clubQuestionRepository.findByClubAndActive(club, 1);
    }

    /** 질문 추가 */
    public ClubQuestion addQuestion(ClubSearch club, String questionText) {
        ClubQuestion q = new ClubQuestion();
        q.setClub(club);
        q.setQuestion(questionText);
        q.setAnswer(null);
        q.setActive(1);
        return clubQuestionRepository.save(q);
    }

    /** 답변 등록/수정 */
    public ClubQuestion updateAnswer(Long questionId, String answer) {
        ClubQuestion q = clubQuestionRepository.findById(questionId)
                .orElseThrow(() -> new NotFoundException("질문을 찾을 수 없습니다."));
        q.setAnswer(answer);
        return clubQuestionRepository.save(q);
    }

    /** 질문 소프트 삭제 */
    public void softDeleteQuestion(Long questionId) {
        ClubQuestion q = clubQuestionRepository.findById(questionId)
                .orElseThrow(() -> new NotFoundException("질문을 찾을 수 없습니다."));
        q.setActive(0);
        clubQuestionRepository.save(q);
    }
}
