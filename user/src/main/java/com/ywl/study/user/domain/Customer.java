package com.ywl.study.user.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by mavlarn on 2018/1/20.
 */
@Entity
@Table(name = "customer")
public class Customer {

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "user_name")
    private String username;

    private String password;

    private String role;

    /*余额*/
    private int deposit;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    /**
     * @return the deposit
     */
    public int getDeposit() {
        return deposit;
    }

    /**
     * @param deposit to set
     */
    public void setDeposit(int deposit) {
        this.deposit = deposit;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", role='" + role + '\'' +
                ", deposit=" + deposit +
                '}';
    }
}
