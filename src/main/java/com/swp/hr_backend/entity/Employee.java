package com.swp.hr_backend.entity;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@NoArgsConstructor
@Table(name = "employee")
@PrimaryKeyJoinColumn(name = "account_id")
public class Employee extends Account {
    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;
}
