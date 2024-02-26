package com.app.FitbitDashBoard.repository;

import com.app.FitbitDashBoard.model.User;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);

    @Query("SELECT u FROM User u ORDER BY u.steps DESC")
    List<User> findTopUsers(PageRequest limit);
}
