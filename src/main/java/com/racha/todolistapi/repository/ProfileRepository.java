package com.racha.todolistapi.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.racha.todolistapi.model.Profile;

public interface ProfileRepository extends JpaRepository<Profile, Long> {
    Optional<Profile> findByUsername(String username);
}
