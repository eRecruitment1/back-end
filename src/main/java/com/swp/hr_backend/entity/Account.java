package com.swp.hr_backend.entity;
import javax.persistence.Column;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

@Entity
@Data
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "account")
public class Account {
    @Id
    @Column(name = "account_id")
    private String accountID;
    @Column(name = "username",unique = true, nullable = false)
    private String username;
    @Column(name = "password",nullable = false)
    private String password;
    @Column(name = "email" ,unique = true,nullable = false)
    private String email;
    @Column(name = "phone",unique = true)
    private String phone;
    @Column(name = "firstname")
    private String firstname;
    @Column(name = "lastname")
    private String lastname;
    @Column(name = "url_img")
    private String urlImg;
    @Column(name = "gender")
    private boolean gender;
    @Column(name = "status" ,nullable = false)
    private boolean status;
}