package com.rbouaro.aimentor.mapper;

import com.rbouaro.aimentor.dto.roadmap.RoadmapResponse;
import com.rbouaro.aimentor.entity.Roadmap;
import org.mapstruct.*;

import java.util.List;

@Mapper(
        componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface RoadmapMapper {

    RoadmapResponse toResponse(Roadmap roadmap);

    List<RoadmapResponse> toResponseList(List<Roadmap> roadmaps);
}