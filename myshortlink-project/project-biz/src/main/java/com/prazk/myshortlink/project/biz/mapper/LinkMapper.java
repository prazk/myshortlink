package com.prazk.myshortlink.project.biz.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.prazk.myshortlink.project.biz.pojo.entity.Link;
import com.prazk.myshortlink.project.pojo.vo.LinkPageVO;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;

public interface LinkMapper extends BaseMapper<Link> {
    void recordAccessLogs(@Param("gid") String gid,
                          @Param("shortUri") String shortUri,
                          @Param("uvIncrement") Long uvIncrement,
                          @Param("ipIncrement") Long ipIncrement);

    IPage<LinkPageVO> pageLink(@Param("page") Page<LinkPageVO> page,
                               @Param("today") LocalDate today,
                               @Param("gid") String gid,
                               @Param("orderTag") String orderTag);

}
