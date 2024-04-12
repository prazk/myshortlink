package com.prazk.myshortlink.project.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LinkStatsIdempotence {
    //ID
    private Long id;
    //消息ID
    private String messageId;
    //消息内容
    private String messageContent;

}

