package com.racha.todolistapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.racha.todolistapi.model.Task;

public interface TaskRepository extends JpaRepository<Task, Long> {
    
} 
