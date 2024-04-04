package com.prazk.myshortlink.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.prazk.myshortlink.project.common.constant.CommonConstant;
import com.prazk.myshortlink.project.common.constant.RedisConstant;
import com.prazk.myshortlink.project.mapper.LinkMapper;
import com.prazk.myshortlink.project.pojo.dto.RecycleAddDTO;
import com.prazk.myshortlink.project.pojo.entity.Link;
import com.prazk.myshortlink.project.service.RecycleBinService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RecycleBinServiceImpl implements RecycleBinService {

    private final LinkMapper linkMapper;
    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public void add(RecycleAddDTO recycleAddDTO) {
        String shortUri = recycleAddDTO.getShortUri();
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
}
