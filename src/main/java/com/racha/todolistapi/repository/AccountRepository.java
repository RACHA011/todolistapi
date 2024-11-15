package com.racha.todolistapi.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.racha.todolistapi.model.Account;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByEmail(String email);

}
