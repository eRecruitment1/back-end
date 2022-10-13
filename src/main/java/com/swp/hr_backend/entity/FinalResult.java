package com.swp.hr_backend.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Setter
@Getter
@NoArgsConstructor
@Table(name = "finalResult")
public class FinalResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "result_id")
    private int resultID;
    @Column(name = "result_status" , nullable = false)
    private String resultStatus;
    @OneToOne
    @JoinColumn(name = "cv_id", nullable = false, unique = true)
    private UserCV userCV;
}
