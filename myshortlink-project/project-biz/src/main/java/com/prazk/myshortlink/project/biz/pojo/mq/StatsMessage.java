package com.prazk.myshortlink.project.biz.pojo.mq;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StatsMessage implements Serializable {

    @Serial
    private static final long serialVersionUID = -6445838443391558491L;

    private String actualIP;
    private String userAgent;
    private String shortUri;
    private String gid;
    private Long uvIncrement;
    private Integer uvCount;
    private String userIdentifier;
}
