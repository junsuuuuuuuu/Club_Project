package com.JoinUs.dp.service;

import com.JoinUs.dp.entity.Club;
import com.JoinUs.dp.entity.User;
import com.JoinUs.dp.repository.ClubRepository;
import com.JoinUs.dp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClubAdminService {

    private final ClubRepository clubRepository;
    private final UserRepository userRepository;

    /** 📊 대시보드 (user + club count) */
    public long getUserCount() {
        return userRepository.count();
    }

    public long getClubCount() {
        return clubRepository.count(); // ✅ Clubs 테이블 기준 집계
    }

    /** 📋 동아리 전체 목록 */
    public List<Club> getAllClubs() {
        return clubRepository.findAll(); // ✅ Clubs 테이블 데이터 반환
    }

    /** 👥 사용자 전체 목록 */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /** ✅ 동아리 승인 처리 */
    @Transactional
    public boolean approveClub(String clubId) {
        return clubRepository.findById(clubId)
                .map(club -> {
                    if (!"approved".equals(club.getStatus())) {
                        club.setStatus("approved");
                        clubRepository.saveAndFlush(club);
                        return true;
                    }
                    return false;
                })
                .orElse(false);
    }
}
