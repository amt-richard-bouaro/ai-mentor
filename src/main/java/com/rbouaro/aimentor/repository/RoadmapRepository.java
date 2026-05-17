package com.rbouaro.aimentor.repository;

import com.rbouaro.aimentor.entity.Roadmap;
import com.rbouaro.aimentor.entity.User;
import com.rbouaro.aimentor.entity.UserGoal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RoadmapRepository extends JpaRepository<Roadmap, Long> {

    Optional<Roadmap> findByUserGoal(UserGoal userGoal);

    boolean existsByUserGoal(UserGoal userGoal);

    List<Roadmap> findByUserGoal_User(User user);

    Optional<Roadmap> findByIdIsAndUserGoal_User(UUID id, User user);
}