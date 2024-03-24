package com.prazk.myshortlink.project.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.prazk.myshortlink.project.common.config.DomainProperties;
import com.prazk.myshortlink.project.common.constant.CommonConstant;
import com.prazk.myshortlink.project.common.constant.LinkConstant;
import com.prazk.myshortlink.project.common.convention.errorcode.BaseErrorCode;
import com.prazk.myshortlink.project.common.convention.exception.ClientException;
import com.prazk.myshortlink.project.mapper.LinkMapper;
import com.prazk.myshortlink.project.pojo.dto.LinkAddDTO;
import com.prazk.myshortlink.project.pojo.dto.LinkPageDTO;
import com.prazk.myshortlink.project.pojo.entity.Link;
import com.prazk.myshortlink.project.pojo.vo.LinkAddVO;
import com.prazk.myshortlink.project.pojo.vo.LinkPageVO;
import com.prazk.myshortlink.project.service.LinkService;
import com.prazk.myshortlink.project.util.HashUtil;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RBloomFilter;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LinkServiceImpl extends ServiceImpl<LinkMapper, Link> implements LinkService {

    private final DomainProperties domainProperties; // 短链接网站域名 my-link.cn
    private final RBloomFilter<String> shortLinkGenerationBloomFilter;

    @Override
    public LinkAddVO addLink(LinkAddDTO linkAddDTO) {
        String originUri = linkAddDTO.getOriginUri(); // https://www.baidu.com
        String domain = linkAddDTO.getDomain(); // www.baidu.com
        StringBuilder uriBuilder = new StringBuilder(originUri);

        String base62;
        int retryCnt = LinkConstant.RETRY_TIMES;
        while (shortLinkGenerationBloomFilter.contains(base62 = HashUtil.linkToBase62(uriBuilder.toString()))) {
            if (retryCnt-- == 0) {
                throw new ClientException(BaseErrorCode.SERVICE_BUSY_ERROR);
            }
            uriBuilder.append(System.currentTimeMillis());
        }
        // 插入布隆过滤器
        shortLinkGenerationBloomFilter.add(base62);

        // 布隆过滤器不存在，则数据库一定不存在，插入数据库
        String fullShortUri = originUri.replaceFirst(domain, domainProperties.getDomain()) + "/" + base62;
        Link link = Link.builder()
                .shortUri(base62)
                .fullShortUri(fullShortUri)
                .build();
        BeanUtil.copyProperties(linkAddDTO, link);
        baseMapper.insert(link);

        LinkAddVO linkAddVO = new LinkAddVO();
        BeanUtil.copyProperties(link, linkAddVO);
        return linkAddVO;
    }

    @Override
    public IPage<LinkPageVO> page(LinkPageDTO linkPageDTO) {
        Page<Link> page = new Page<>(linkPageDTO.getPage(), linkPageDTO.getPageSize());
        LambdaQueryWrapper<Link> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Link::getGid, linkPageDTO.getGid())
                .eq(Link::getDelFlag, CommonConstant.NOT_DELETED);
        baseMapper.selectPage(page, wrapper);
        // 获取查询结果
        IPage<LinkPageVO> result = page.convert(each -> BeanUtil.toBean(each, LinkPageVO.class));
        return result;
    }
}
