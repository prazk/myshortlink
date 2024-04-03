package com.prazk.myshortlink.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.prazk.myshortlink.project.common.constant.CommonConstant;
import com.prazk.myshortlink.project.common.convention.errorcode.BaseErrorCode;
import com.prazk.myshortlink.project.common.convention.exception.ClientException;
import com.prazk.myshortlink.project.mapper.LinkGotoMapper;
import com.prazk.myshortlink.project.mapper.LinkMapper;
import com.prazk.myshortlink.project.pojo.dto.LinkRestoreDTO;
import com.prazk.myshortlink.project.pojo.entity.Link;
import com.prazk.myshortlink.project.pojo.entity.LinkGoto;
import com.prazk.myshortlink.project.service.LinkGotoService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.redisson.api.RBloomFilter;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LinkGotoServiceImpl extends ServiceImpl<LinkGotoMapper, LinkGoto> implements LinkGotoService {

    private final RBloomFilter<String> shortLinkGenerationBloomFilter;
    private final LinkMapper linkMapper;

    @SneakyThrows
    @Override
    public void restore(LinkRestoreDTO linkRestoreDTO, HttpServletRequest request, HttpServletResponse response) {
        // 先查询布隆过滤器，再查询数据库
        String shortUri = linkRestoreDTO.getShortUri();
        if (!shortLinkGenerationBloomFilter.contains(shortUri)) {
            // 布隆过滤器不存在，则实际一定不存在，说明是无效短链接
            throw new ClientException(BaseErrorCode.LINK_NOT_EXISTS_ERROR);
        }
        // 通过路由表的分片键【short_uri】，拿到短链接表的分片键【gid】
        LambdaQueryWrapper<LinkGoto> linkGotoWrapper = new LambdaQueryWrapper<>();
        linkGotoWrapper.eq(LinkGoto::getShortUri, shortUri);
        LinkGoto linkGoto = getOne(linkGotoWrapper);
        if (linkGoto == null) {
            throw new ClientException(BaseErrorCode.SERVICE_ERROR);
        }
        String gid = linkGoto.getGid();
        // 根据短链接表的分片键【gid】，查询数据库
        LambdaQueryWrapper<Link> linkWrapper = new LambdaQueryWrapper<>();
        linkWrapper.eq(Link::getGid, gid)
                .eq(Link::getShortUri, shortUri)
                .eq(Link::getEnableStatus, CommonConstant.HAS_ENABLED)
                .eq(Link::getDelFlag, CommonConstant.NOT_DELETED);
        Link link = linkMapper.selectOne(linkWrapper);
        if (link == null) {
            throw new ClientException(BaseErrorCode.SERVICE_ERROR);
        }
        response.sendRedirect(link.getOriginUri());
    }
}
