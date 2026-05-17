package com.rbouaro.aimentor.event;

import com.rbouaro.aimentor.entity.UserGoal;

public record GoalCreatedEvent(UserGoal userGoal) {}