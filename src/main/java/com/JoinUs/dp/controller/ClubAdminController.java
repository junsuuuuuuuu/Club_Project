package com.JoinUs.dp.controller;

import com.JoinUs.dp.entity.Club;
import com.JoinUs.dp.entity.User;
import com.JoinUs.dp.service.ClubAdminService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/clubs/admin")
public class ClubAdminController {

    private final ClubAdminService clubAdminService;

    public ClubAdminController(ClubAdminService clubAdminService) {
        this.clubAdminService = clubAdminService;
    }

    /** 📊 대시보드 */
    @GetMapping("/dashboard")
    public Map<String, Long> getDashboard() {
        Map<String, Long> result = new HashMap<>();
        result.put("userCount", clubAdminService.getUserCount());
        result.put("clubCount", clubAdminService.getClubCount());
        return result;
    }

    /** 📋 동아리 목록 */
    @GetMapping("/clubs")
    public List<Club> getAllClubs() { // ✅ Club 타입으로 반환
        return clubAdminService.getAllClubs();
    }

    /** 👥 사용자 목록 */
    @GetMapping("/users")
    public List<User> getUsers() {
        return clubAdminService.getAllUsers();
    }

    /** ✅ 동아리 승인 */
    @PatchMapping("/clubs/{clubId}/approve")
    public boolean approveClub(@PathVariable String clubId) {
        return clubAdminService.approveClub(clubId);
    }
}
