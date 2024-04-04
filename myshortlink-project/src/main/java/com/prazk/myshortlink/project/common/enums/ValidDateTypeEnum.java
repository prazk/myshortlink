package com.prazk.myshortlink.project.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;


@AllArgsConstructor
public enum ValidDateTypeEnum {
    PERMANENT(0),
    CUSTOMIZED(1);

    @Getter
    private int type;

    public static ValidDateTypeEnum fromType(int type) {
        for (ValidDateTypeEnum enumValue : ValidDateTypeEnum.values()) {
            if (enumValue.getType() == type) {
                return enumValue;
            }
        }
        throw new IllegalArgumentException("Invalid type value: " + type);
    }
}
