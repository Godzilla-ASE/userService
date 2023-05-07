package com.example.userservice.entity;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

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
    @Temporal(TemporalType.DATE)
    private Date birthday;

    @Column(name = "token")
    private String token;

    @Column(name = "creationDate")
    @Temporal(TemporalType.DATE)
    private Date creationDate;

    @Column(name = "email")
    private String email;

    @Column(name = "location")
    private String location;

    @Column(name = "fans")
    private String fans = "";

    @Column(name = "followings")
    private String followings= "";

    @Column(name = "haters")
    private String haters= "";

    @Column(name = "avatarUrl")
    private String avatarUrl = "https://robohash.org/31.10.156.227.png";

}