package com.github.elysalin18.cmpt276groupproject.donatedesk.models;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {
    List<User> findByNameAndPassword(String name, String password);
    boolean existsByNameAndPassword(String name, String password);
}