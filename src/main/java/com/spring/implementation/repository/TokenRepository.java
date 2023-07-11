package com.spring.implementation.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.spring.implementation.model.PasswordResetToken;

public interface TokenRepository extends JpaRepository<PasswordResetToken, Integer>{

	PasswordResetToken findByToken(String token);

}
