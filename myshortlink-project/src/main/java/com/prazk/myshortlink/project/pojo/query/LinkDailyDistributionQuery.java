package com.prazk.myshortlink.project.pojo.query;

import lombok.Data;

/**
 * 24小时分布查询结果对象
 */
@Data
public class LinkDailyDistributionQuery {
    private Integer cnt;
    private Integer hour;
}
