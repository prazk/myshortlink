package com.prazk.myshortlink.project.biz.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.prazk.myshortlink.project.biz.common.constant.CommonConstant;
import com.prazk.myshortlink.project.biz.common.constant.RedisConstant;
import com.prazk.myshortlink.project.biz.mapper.LinkMapper;
import com.prazk.myshortlink.project.biz.pojo.entity.Link;
import com.prazk.myshortlink.project.biz.service.RecycleBinService;
import com.prazk.myshortlink.project.biz.util.LinkUtil;
import com.prazk.myshortlink.project.pojo.dto.RecycleAddDTO;
import com.prazk.myshortlink.project.pojo.dto.RecycleDeleteDTO;
import com.prazk.myshortlink.project.pojo.dto.RecyclePageDTO;
import com.prazk.myshortlink.project.pojo.dto.RecycleRecoverDTO;
import com.prazk.myshortlink.project.pojo.vo.RecyclePageVO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecycleBinServiceImpl implements RecycleBinService {

    private final LinkMapper linkMapper;
    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public void add(RecycleAddDTO recycleAddDTO) {
//        String shortUri = recycleAddDTO.getShortUri();
        String shortUri = LinkUtil.getShortUriByFullShortUrl(recycleAddDTO.getFullShortUrl());
        // 根据【gid】和【shortUri】更新短链接状态
        LambdaUpdateWrapper<Link> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Link::getGid, recycleAddDTO.getGid())
                .eq(Link::getShortUri, shortUri)
                .eq(Link::getDelFlag, CommonConstant.NOT_DELETED)
                .eq(Link::getEnableStatus, CommonConstant.HAS_ENABLED);

        Link link = Link.builder().enableStatus(CommonConstant.NOT_ENABLED).build();
        linkMapper.update(link, wrapper);

        // 删除缓存中的内容
        String key = RedisConstant.GOTO_SHORT_LINK_KEY_PREFIX + shortUri;
        stringRedisTemplate.delete(key);
    }

    @Override
    public void delete(RecycleDeleteDTO recycleDeleteDTO) {
//        String shortUri = recycleDeleteDTO.getShortUri();
        String shortUri = LinkUtil.getShortUriByFullShortUrl(recycleDeleteDTO.getFullShortUrl());
        // 根据【gid】和【shortUri】删除短链接
        LambdaUpdateWrapper<Link> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Link::getGid, recycleDeleteDTO.getGid())
                .eq(Link::getShortUri, shortUri)
                .eq(Link::getDelFlag, CommonConstant.NOT_DELETED)
                .eq(Link::getEnableStatus, CommonConstant.NOT_ENABLED);

        // 设置【del_flag】为已删除
        Link link = Link.builder().delFlag(CommonConstant.HAS_DELETED).build();
        linkMapper.update(link, wrapper);
    }

    @Override
    public void recover(RecycleRecoverDTO recycleRecoverDTO) {
//        String shortUri = recycleRecoverDTO.getShortUri();
        String shortUri = LinkUtil.getShortUriByFullShortUrl(recycleRecoverDTO.getFullShortUrl());

        LambdaUpdateWrapper<Link> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Link::getGid, recycleRecoverDTO.getGid())
                .eq(Link::getShortUri, shortUri)
                .eq(Link::getDelFlag, CommonConstant.NOT_DELETED)
                .eq(Link::getEnableStatus, CommonConstant.NOT_ENABLED);

        Link link = Link.builder().enableStatus(CommonConstant.HAS_ENABLED).build();
        linkMapper.update(link, wrapper);

        // 删除缓存中的空值（这里不做预热）
        String key = RedisConstant.GOTO_SHORT_LINK_KEY_PREFIX + shortUri;
        stringRedisTemplate.delete(key);
    }

    @Override
    public Page<RecyclePageVO> page(RecyclePageDTO recyclePageDTO) {
        Page<Link> page = new Page<>(recyclePageDTO.getCurrent(), recyclePageDTO.getSize());
        List<String> gidList = recyclePageDTO.getGid();

        if (gidList.isEmpty()) {
            return new Page<>();
        }

        QueryWrapper<Link> wrapper = new QueryWrapper<>();
        wrapper.select("id","domain","short_uri","full_short_uri AS fullShortUrl","origin_uri AS originUrl","total_pv","total_uv","total_uip","gid","enable_status","created_type","valid_date_type","valid_date","`description` AS `describe`","create_time","update_time","del_flag")
                .lambda()
                .in(Link::getGid, gidList)
                .eq(Link::getDelFlag, CommonConstant.NOT_DELETED)
                .eq(Link::getEnableStatus, CommonConstant.NOT_ENABLED);

        linkMapper.selectPage(page, wrapper);

        // 获取查询结果
        return (Page<RecyclePageVO>) page.convert(each -> BeanUtil.toBean(each, RecyclePageVO.class));
    }
}
