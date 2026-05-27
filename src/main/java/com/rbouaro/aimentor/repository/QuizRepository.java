package com.rbouaro.aimentor.repository;

import com.rbouaro.aimentor.entity.Milestone;
import com.rbouaro.aimentor.entity.Quiz;
import com.rbouaro.aimentor.entity.Roadmap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long> {
    Optional<Quiz> findByMilestone(Milestone milestone);
    Optional<Quiz> findByRoadmap(Roadmap roadmap);
}