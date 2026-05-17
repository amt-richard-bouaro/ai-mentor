package com.rbouaro.aimentor.mapper;

import com.rbouaro.aimentor.dto.goal.UserGoalResponse;
import com.rbouaro.aimentor.entity.UserGoal;
import org.mapstruct.*;

import java.util.List;

@Mapper(
        componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface UserGoalMapper {

    UserGoalResponse toResponse(UserGoal userGoal);

    List<UserGoalResponse> toResponseList(List<UserGoal> userGoals);
}