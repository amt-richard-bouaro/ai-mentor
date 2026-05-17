package com.rbouaro.aimentor.mapper;

import com.rbouaro.aimentor.dto.resource.ResourceResponse;
import com.rbouaro.aimentor.entity.Resource;
import org.mapstruct.*;

import java.util.List;

@Mapper(
        componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface ResourceMapper {

    @Mapping(source = "milestone.id", target = "milestoneId")
    ResourceResponse toResponse(Resource resource);

    @IterableMapping(elementTargetType = ResourceResponse.class)
    List<ResourceResponse> toResponseList(List<Resource> resources);
}