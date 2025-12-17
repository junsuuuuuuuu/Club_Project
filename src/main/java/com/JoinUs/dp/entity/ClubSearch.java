package com.JoinUs.dp.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "clubs") // 
public class ClubSearch {

    @Id
    @Column(name = "club_id")
    private String clubId;

    @Column(name = "name") 
    private String name;

    @Column(name = "member_count")
    private Integer memberCount;

    @Column(name = "recruiting")
    private Boolean recruiting;

    // ??Getter / Setter
    public String getClubId() { return clubId; }
    public void setClubId(String clubId) { this.clubId = clubId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Integer getMemberCount() { return memberCount; }
    public void setMemberCount(Integer memberCount) { this.memberCount = memberCount; }

    public Boolean getRecruiting() { return recruiting; }
    public void setRecruiting(Boolean recruiting) { this.recruiting = recruiting; }
}
