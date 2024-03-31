package com.prazk.myshortlink.project.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;


@AllArgsConstructor
public enum ValidDateTypeEnum {
    PERMANENT(0),
    CUSTOMIZED(1);

    @Getter
    private int type;
}
