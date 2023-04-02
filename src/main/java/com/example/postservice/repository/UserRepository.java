package com.example.postservice.repository;

import com.example.postservice.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository("PostRepository")
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findById(Long id);
}
