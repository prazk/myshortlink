package com.prazk.myshortlink.project.pojo.vo;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.prazk.myshortlink.project.pojo.serializer.DeviceDeserializer;
import com.prazk.myshortlink.project.pojo.serializer.DeviceSerializer;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
public class LinkDeviceStatsVO extends LinkInfoStatsAbstractVO {
    @JsonSerialize(using = DeviceSerializer.class)
    @JsonDeserialize(using = DeviceDeserializer.class)
    private Integer device;
}
