package com.swp.hr_backend.entity;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Date;
import java.util.List;

@Entity
@Setter
@Getter
@NoArgsConstructor
@Table(name = "schedule")
public class Schedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "schedule_id")
    private int scheduleID;

    @Column(name = "date", nullable = false)
    private Date date;

    @OneToMany(mappedBy = "schedule",cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    private List<ScheduleDetail> scheduleDetails;
}
