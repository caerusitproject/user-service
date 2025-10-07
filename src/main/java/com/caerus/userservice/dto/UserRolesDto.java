package com.caerus.userservice.dto;

import java.util.Set;

public record UserRolesDto(
         String email,
         Set<String>roles
) {
}
