package com.swp.hr_backend.entity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    @Column(name = "account_id", nullable = false)
    private String accountID;
    @Column(name = "username",unique = true, nullable = false, length = 50)
    private String username;
    @Column(name = "password", length = 50)
    private String password;
    @Column(name = "email" ,unique = true,nullable = false, length = 100)
    private String email;
    @Column(name = "phone",length = 10)
    private String phone;
    @Column(name = "firstname", length = 20)
    private String firstname;
    @Column(name = "lastname", length = 20)
    private String lastname;
    @Column(name = "url_img", length = 2084)
    private String urlImg;
    @Column(name = "gender", nullable = false)
    private boolean gender;
    @Column(name = "status", nullable = false)
    private boolean status;
}