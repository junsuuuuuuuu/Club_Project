package com.JoinUs.dp.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "CLUB_QUESTION")
public class ClubQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @jakarta.persistence.Column(name = "id")
    private Long qid;  // id → qid

    @ManyToOne
    @JoinColumn(name = "club_id", referencedColumnName = "club_id")
    private ClubSearch club;


    private String question;
    private String answer;
    private int active; // 1 = 활성, 0 = 삭제

    // Getter / Setter
    public Long getQid() { return qid; }
    public void setQid(Long qid) { this.qid = qid; }

    public ClubSearch getClub() {
        return club;
    }
    public void setClub(ClubSearch club) {
        this.club = club;
    }

    public String getQuestion() {
        return question;
    }
    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }
    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public int getActive() {
        return active;
    }
    public void setActive(int active) {
        this.active = active;
    }
}
