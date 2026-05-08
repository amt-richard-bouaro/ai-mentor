package com.rbouaro.aimentor.mapper;

import com.rbouaro.aimentor.dto.user.UserProfile;
import com.rbouaro.aimentor.dto.user.UserRegisterRequest;
import com.rbouaro.aimentor.entity.User;
import com.rbouaro.aimentor.constants.enums.UserPermission;
import org.mapstruct.*;
import org.mapstruct.Named;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;


@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface UserMapper {

//    @Mapping(target = "permissions", source = "permissions", qualifiedByName = "permissionsToStrings")
    UserProfile toResponse(User user);

    List<UserProfile> toResponseList(List<User> users);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "permissions", ignore = true)
    @Mapping(target = "goals", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "lastModifiedAt", ignore = true)
    User fromRegister(UserRegisterRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "permissions", ignore = true)
    @Mapping(target = "goals", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "lastModifiedAt", ignore = true)
    void updateFromRegister(UserRegisterRequest request, @MappingTarget User user);

    @Named("permissionsToStrings")
    default Set<String> permissionsToStrings(Set<UserPermission> permissions) {
        if (permissions == null) return Set.of();
        return permissions.stream().map(Enum::name).collect(Collectors.toUnmodifiableSet());
    }

    @Named("stringsToPermissions")
    default Set<UserPermission> stringsToPermissions(Collection<String> names) {
        if (names == null) return Set.of();
        return names.stream()
                .map(s -> {
                    try {
                        return UserPermission.valueOf(s);
                    } catch (IllegalArgumentException ex) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }
}