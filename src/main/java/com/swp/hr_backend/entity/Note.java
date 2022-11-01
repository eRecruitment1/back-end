package com.swp.hr_backend.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Setter
@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Table(name = "note")
public class Note {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "note_id")
    private int noteID;
    @Column(name = "message", length = 1700 , nullable = false)
    private String message;
    @Column(name ="point", nullable = false)
    private int point;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cv_id",nullable = false)
    @JoinColumn(name = "account_id",nullable = false)
    @JoinColumn(name = "schedule_id",nullable = false)
    private ScheduleDetail scheduleDetail;

}
