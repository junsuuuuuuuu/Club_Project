package com.JoinUs.dp.service;

import com.JoinUs.dp.common.exception.BadRequestException;
import com.JoinUs.dp.common.exception.ConflictException;
import com.JoinUs.dp.common.exception.NotFoundException;
import com.JoinUs.dp.dto.WishlistResponse;
import com.JoinUs.dp.entity.Club;
import com.JoinUs.dp.entity.User;
import com.JoinUs.dp.entity.Wishlist;
import com.JoinUs.dp.repository.ClubRepository;
import com.JoinUs.dp.repository.UserRepository;
import com.JoinUs.dp.repository.WishlistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WishlistService {

    private final WishlistRepository wishlistRepository;
    private final UserRepository userRepository;
    private final ClubRepository clubRepository;

    /** 찜추가 */
    public WishlistResponse addWishlist(Long userId, String clubId) {
        Long parsedClubId = parseClubId(clubId);

        if (wishlistRepository.existsByUserIdAndClubClubId(userId, parsedClubId)) {
            throw new ConflictException("이미 찜한 클럽입니다.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("사용자를 찾을 수 없습니다."));
        Club club = clubRepository.findById(parsedClubId)
                .orElseThrow(() -> new NotFoundException("클럽을 찾을 수 없습니다."));

        Wishlist wish = new Wishlist(user, club);
        wishlistRepository.save(wish);

        return new WishlistResponse(
                String.valueOf(club.getClubId()),
                club.getName(),
                club.getType(),
                club.getCategory(),
                club.getDepartment()
        );
    }

    /** 찜삭제 */
    public void deleteWishlist(Long userId, String clubId) {
        Long parsedClubId = parseClubId(clubId);
        Wishlist wish = wishlistRepository.findByUserIdAndClubClubId(userId, parsedClubId)
                .orElseThrow(() -> new NotFoundException("찜한 내역을 찾을 수 없습니다."));

        wishlistRepository.delete(wish);
    }

    /** 내 찜목록 조회 (type 필터 optional) */
    public List<WishlistResponse> getWishlist(Long userId, String type) {

        return wishlistRepository.findByUserIdAndClubType(userId, type).stream()
                .map(w -> new WishlistResponse(
                        String.valueOf(w.getClub().getClubId()),
                        w.getClub().getName(),
                        w.getClub().getType(),
                        w.getClub().getCategory(),
                        w.getClub().getDepartment()
                ))
                .toList();
    }

    /** 일반동아리 카테고리별 */
    public List<WishlistResponse> getGeneralByCategory(Long userId, String category) {

        return wishlistRepository.findGeneralByUserIdAndCategory(userId, category).stream()
                .map(w -> new WishlistResponse(
                        String.valueOf(w.getClub().getClubId()),
                        w.getClub().getName(),
                        w.getClub().getType(),
                        w.getClub().getCategory(),
                        w.getClub().getDepartment()
                ))
                .toList();
    }

    /** 전공동아리 학과별 */
    public List<WishlistResponse> getMajorByDepartment(Long userId, String department) {

        return wishlistRepository.findMajorByUserIdAndDepartment(userId, department).stream()
                .map(w -> new WishlistResponse(
                        String.valueOf(w.getClub().getClubId()),
                        w.getClub().getName(),
                        w.getClub().getType(),
                        w.getClub().getCategory(),
                        w.getClub().getDepartment()
                ))
                .toList();
    }

    private Long parseClubId(String clubId) {
        if (clubId == null) {
            throw new BadRequestException("clubId는 필수입니다.");
        }
        String trimmed = clubId.trim();
        if (trimmed.toLowerCase().startsWith("sg")) {
            trimmed = trimmed.substring(2);
        }
        try {
            return Long.parseLong(trimmed);
        } catch (NumberFormatException e) {
            throw new BadRequestException("clubId 형식이 올바르지 않습니다.");
        }
    }
}
