package com.caerus.userservice.repository;
import com.caerus.userservice.model.User;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	
	Optional<User> findById(Long userId);
	Optional<User> findByEmail(String email);

	Boolean existsByemail(String email);

	 
}