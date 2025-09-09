package com.caerus.userservice.mapper;

import com.caerus.userservice.domain.User;
import com.caerus.userservice.dto.RegisterRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RegisterMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "username", ignore = true)
    User toEntity(RegisterRequest request);

}
