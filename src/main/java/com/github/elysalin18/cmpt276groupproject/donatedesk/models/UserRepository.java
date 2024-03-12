package com.github.elysalin18.cmpt276groupproject.donatedesk.models;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {
    boolean existsByNameAndPassword(String name, String password);
}