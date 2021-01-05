package com.assignments.assignment5.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.assignments.assignment5.models.User;

public interface UserRepository extends JpaRepository<User,Integer>{

	Optional<User> findByUsername(String username);
	
	Boolean existsByUsername(String username);
	
}
