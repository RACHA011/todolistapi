package com.racha.todolistapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.racha.todolistapi.model.Team;

public interface TeamRepository extends JpaRepository<Team, Long> {

}
