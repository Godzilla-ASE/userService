package com.example.userservice.entity;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "birthday")
    private String birthday;

    @Column(name = "token")
    private String token;

    @Column(name = "creationDate")
    private String creationDate;

    @Column(name = "email")
    private String email;

    @Column(name = "location")
    private String location;

    @Column(name = "fans")
    private String fans;

    @Column(name = "followings")
    private String followings;

    @Column(name = "haters")
    private String haters;


}