package com.racha.todolistapi.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.racha.todolistapi.model.Team;
import com.racha.todolistapi.repository.TeamRepository;

@Service
public class TeamService {
    @Autowired
    TeamRepository teamRepository;

    public Team save(Team team) {
        return teamRepository.save(team);
    }

    public List<Team> findAll() {
        return teamRepository.findAll();
    }

    public Optional<Team> findById(Long id) {
        return teamRepository.findById(id);
    }

    public void delete(Team team) {
        teamRepository.delete(team);
    }
}
