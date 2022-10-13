package com.swp.hr_backend.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;

@Entity
@Setter
@Getter
@NoArgsConstructor
@Table(name = "userCV")
public class UserCV {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cv_id")
    private int cvID;
    @Column(name = "apply_time" , nullable = false)
    private Timestamp applyTime;
    @Column(name = "linkCV", length = 1024, nullable = false)
    private String linkCV;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;
    @OneToOne
    @JoinColumn(name = "account_id",unique = true, nullable = false)
    private Candidate candidate;
    @OneToOne(mappedBy = "userCV", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private FinalResult finalResult;
    @OneToMany(mappedBy = "userCV",cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    private List<ScheduleDetail> scheduleDetails;
}
