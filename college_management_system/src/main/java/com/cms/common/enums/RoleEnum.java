package com.cms.common.enums;



import com.fasterxml.jackson.annotation.JsonValue;

public enum RoleEnum {
    STUDENT("STUDENT"),
    DRIVER("DRIVER"),
    ADMIN("ADMIN");

    private final String roleName;

    RoleEnum(String roleName) {
        this.roleName = roleName;
    }

    @JsonValue
    public String getRoleName() {
        return roleName;
    }
}
