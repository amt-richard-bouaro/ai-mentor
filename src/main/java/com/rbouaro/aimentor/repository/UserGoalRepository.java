package com.rbouaro.aimentor.repository;

import com.rbouaro.aimentor.model.User;
import com.rbouaro.aimentor.model.UserGoal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserGoalRepository extends JpaRepository<UserGoal, Long> {
    
    List<UserGoal> findByUser(User user);
    
    List<UserGoal> findByUserAndStatus(User user, UserGoal.GoalStatus status);
    
    Optional<UserGoal> findTopByUserOrderByCreatedAtDesc(User user);
}