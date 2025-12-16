package com.JoinUs.dp.controller;

import com.JoinUs.dp.common.response.Response;
import com.JoinUs.dp.dto.WishlistResponse;
import com.JoinUs.dp.global.common.api.ApiPath;
import com.JoinUs.dp.service.WishlistService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class WishlistController {

    private final WishlistService wishlistService;

    /** 찜 추가 */
    @PostMapping(ApiPath.WISHLIST)
    public ResponseEntity<Response<WishlistResponse>> addWishlist(
            @RequestParam Long userId,
            @RequestParam String clubId
    ) {
        WishlistResponse data = wishlistService.addWishlist(userId, clubId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new Response<>(201, data, "찜 추가 완료"));
    }

    /** 찜 삭제 */
    @DeleteMapping(ApiPath.WISHLIST + "/{clubId}")
    public ResponseEntity<Response<Void>> deleteWishlist(
            @RequestParam Long userId,
            @PathVariable String clubId
    ) {
        wishlistService.deleteWishlist(userId, clubId);
        return ResponseEntity.ok(new Response<>(200, null, "찜 삭제 완료"));
    }

    /** 전체 조회 + 타입 필터 */
    @GetMapping(ApiPath.WISHLIST)
    public ResponseEntity<Response<List<WishlistResponse>>> getWishlist(
            @RequestParam Long userId,
            @RequestParam(required = false) String type
    ) {
        List<WishlistResponse> list = wishlistService.getWishlist(userId, type);
        return ResponseEntity.ok(new Response<>(200, list, "찜 목록 조회 성공"));
    }

    /** 일반동아리 카테고리별 */
    @GetMapping(ApiPath.WISHLIST_GENERAL_CATEGORY)
    public ResponseEntity<Response<List<WishlistResponse>>> getGeneralByCategory(
            @RequestParam("userId") Long userId,
            @PathVariable("category") String category  // ✅ 여기에도 명시
    ) {
        List<WishlistResponse> list = wishlistService.getGeneralByCategory(userId, category);
        return ResponseEntity.ok(new Response<>(200, list, "일반동아리 카테고리별 찜 목록 조회 성공"));
    }


    /** 전공동아리 학과별 */
    @GetMapping(ApiPath.WISHLIST_MAJOR_DEPARTMENT)
    public ResponseEntity<Response<List<WishlistResponse>>> getMajorByDept(
            @RequestParam("userId") Long userId,
            @PathVariable("department") String department  // ✅ 명시적 바인딩
    ) {
        List<WishlistResponse> list = wishlistService.getMajorByDepartment(userId, department);
        return ResponseEntity.ok(new Response<>(200, list, "전공동아리 학과별 찜 목록 조회 성공"));
    }

}
