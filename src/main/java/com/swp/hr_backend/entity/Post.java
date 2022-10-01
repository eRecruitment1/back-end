package com.swp.hr_backend.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

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
    @Column(name = "start_time" , nullable = false)
    private Timestamp startTime;
    @Column(name ="description_post",nullable = false)
    private String description;
    @Column(name = "thumbnail_url",nullable = false)
    private String thumbnailUrl;
    @Column(name = "title",nullable = false)
    private String title;
    @Column(name = "status",nullable = false)
    private boolean status;
    @ManyToOne
    @JoinColumn(name = "account_id")
    private Employee employee;
}