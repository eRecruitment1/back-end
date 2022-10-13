package com.swp.hr_backend.entity;

import java.sql.Timestamp;
import java.util.List;

import javax.persistence.*;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@NoArgsConstructor
@Table(name = "post")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private int postID;
    @Column(name = "start_time", nullable = false)
    private Timestamp startTime;
    @Column(name = "description_post", nullable = false, length = 2084)
    private String description;
    @Column(name = "thumbnail_url", nullable = false, length = 2084)
    private String thumbnailUrl;
    @Column(name = "title", length = 500, nullable = false)
    private String title;
    @Column(name = "status", nullable = false)
    private boolean status;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Employee employee;
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<UserCV> listUserCv;
}