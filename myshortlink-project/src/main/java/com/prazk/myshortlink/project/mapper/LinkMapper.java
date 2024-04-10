package com.prazk.myshortlink.project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.prazk.myshortlink.project.pojo.entity.Link;
import org.apache.ibatis.annotations.Param;

public interface LinkMapper extends BaseMapper<Link> {
    void recordAccessLogs(@Param("gid") String gid,
                          @Param("shortUri") String shortUri,
                          @Param("uvIncrement") Long uvIncrement,
                          @Param("ipIncrement") Long ipIncrement);
}
