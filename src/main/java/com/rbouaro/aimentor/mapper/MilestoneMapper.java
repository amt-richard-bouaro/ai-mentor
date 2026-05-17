package com.rbouaro.aimentor.mapper;

import com.rbouaro.aimentor.dto.milestone.MilestoneResponse;
import com.rbouaro.aimentor.entity.Milestone;
import org.mapstruct.*;

import java.util.List;

@Mapper(
        componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface MilestoneMapper {

    @Mapping(source = "roadmap.id", target = "roadmapId")
    MilestoneResponse toResponse(Milestone milestone);

    @IterableMapping(elementTargetType = MilestoneResponse.class)
    List<MilestoneResponse> toResponseList(List<Milestone> milestones);
}