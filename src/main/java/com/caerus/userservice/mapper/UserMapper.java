package com.caerus.userservice.mapper;

import com.caerus.userservice.domain.Role;
import com.caerus.userservice.domain.User;
import com.caerus.userservice.dto.RegisterRequest;
import com.caerus.userservice.dto.UserRolesDto;
import com.caerus.userservice.dto.UserUpdateDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "username", ignore = true)
    @Mapping(target = "roles", ignore = true)
    User toEntity(RegisterRequest request);

    @Mapping(target = "roles", expression = "java(mapRolesToDto(user.getRoles()))")
    RegisterRequest toDto(User user);

    @Mapping(target = "roles", expression = "java(mapRolesToDto(user.getRoles()))")
    UserRolesDto toUserRolesDto(User user);

    @Mapping(target = "roles", ignore = true)
    void updateUserFromDto(UserUpdateDto dto, @MappingTarget User entity);

    default Set<String> mapRolesToDto(Set<Role> roles) {
        if (roles == null) return Set.of();
        return roles.stream()
                .map(Role::getName)
                .collect(Collectors.toSet());
    }
}
