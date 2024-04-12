package com.prazk.myshortlink.project.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.prazk.myshortlink.project.common.config.DomainProperties;
import com.prazk.myshortlink.project.common.constant.CommonConstant;
import com.prazk.myshortlink.project.common.constant.LinkConstant;
import com.prazk.myshortlink.project.common.constant.RedisConstant;
import com.prazk.myshortlink.project.common.convention.errorcode.BaseErrorCode;
import com.prazk.myshortlink.project.common.convention.exception.ClientException;
import com.prazk.myshortlink.project.common.enums.ValidDateTypeEnum;
import com.prazk.myshortlink.project.mapper.LinkMapper;
import com.prazk.myshortlink.project.mapper.LinkStatsTodayMapper;
import com.prazk.myshortlink.project.pojo.dto.LinkAddDTO;
import com.prazk.myshortlink.project.pojo.dto.LinkCountDTO;
import com.prazk.myshortlink.project.pojo.dto.LinkPageDTO;
import com.prazk.myshortlink.project.pojo.dto.LinkUpdateDTO;
import com.prazk.myshortlink.project.pojo.entity.Link;
import com.prazk.myshortlink.project.pojo.entity.LinkGoto;
import com.prazk.myshortlink.project.pojo.query.LinkTodayLogsQuery;
import com.prazk.myshortlink.project.pojo.vo.LinkAddVO;
import com.prazk.myshortlink.project.pojo.vo.LinkCountVO;
import com.prazk.myshortlink.project.pojo.vo.LinkPageVO;
import com.prazk.myshortlink.project.service.LinkGotoService;
import com.prazk.myshortlink.project.service.LinkService;
import com.prazk.myshortlink.project.util.HashUtil;
import com.prazk.myshortlink.project.util.LinkUtil;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RBloomFilter;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Method;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LinkServiceImpl extends ServiceImpl<LinkMapper, Link> implements LinkService {

    private final DomainProperties domainProperties; // 短链接网站域名 my-link.cn，改hosts
    private final RBloomFilter<String> shortLinkGenerationBloomFilter;
    private final LinkGotoService linkGotoService;
    private final StringRedisTemplate stringRedisTemplate;
    private final LinkStatsTodayMapper linkStatsTodayMapper;

    @Override
    @Transactional
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
        // 将base62的6位短链接插入布隆过滤器
        // 布隆过滤器容量1亿，短链接数量62^6=568亿，murmurhash2^32=42亿
        shortLinkGenerationBloomFilter.add(base62);

        // 布隆过滤器不存在，则数据库一定不存在，插入数据库
        String fullShortUri = originUri.replaceFirst(domain, domainProperties.getDomain()) + "/" + base62;
        Link link = Link.builder()
                .shortUri(base62)
                .fullShortUri(fullShortUri)
                .build();
        BeanUtil.copyProperties(linkAddDTO, link);
        baseMapper.insert(link);
        // 插入路由表
        LinkGoto linkGoto = LinkGoto.builder().gid(link.getGid()).shortUri(link.getShortUri()).build();
        linkGotoService.save(linkGoto);

        LinkAddVO linkAddVO = new LinkAddVO();
        BeanUtil.copyProperties(link, linkAddVO);
        // 缓存预热：先操作数据库，再操作缓存
        String shortUri = link.getShortUri();
        String key = RedisConstant.GOTO_SHORT_LINK_KEY_PREFIX + shortUri;
        Duration expire = LinkUtil.getLinkExpireDuraion(ValidDateTypeEnum.fromType(link.getValidDateType()), link.getValidDate());
        if (Duration.ZERO.equals(expire)) {
            throw new ClientException(BaseErrorCode.LINK_EXPIRED_ERROR);
        }
        stringRedisTemplate.opsForValue().set(key, link.getOriginUri(), expire);
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
        CopyOptions copyOptions = CopyOptions.create()
                .setFieldMapping(Map.of("pv", "pvTotal", "uv", "uvTotal", "ip", "ipTotal"));
        IPage<LinkPageVO> result = page.convert(each -> BeanUtil.toBean(each, LinkPageVO.class, copyOptions));
        // 封装今日查询数据
        Set<String> shortUris = result.getRecords().stream().map(LinkPageVO::getShortUri).collect(Collectors.toSet());
        if (!shortUris.isEmpty()) {
            Map<String, Object> map = linkStatsTodayMapper.selectTodayLogs(shortUris, LocalDate.now());
            // 封装今日UV PV IP
            result.getRecords().forEach(record -> {
                String shortUri = record.getShortUri();
                LinkTodayLogsQuery linkTodayLogsQuery = BeanUtil.toBean(map.get(shortUri), LinkTodayLogsQuery.class);
                record.setPvDaily(linkTodayLogsQuery.getTodayPv());
                record.setIpDaily(linkTodayLogsQuery.getTodayUip());
                record.setUvDaily(linkTodayLogsQuery.getTodayUv());
            });
        }
        // 按orderTag排序
        String orderTag = StrUtil.isBlank(linkPageDTO.getOrderTag()) ? "createTime" : linkPageDTO.getOrderTag();
        List<LinkPageVO> records = result.getRecords();
        records.sort((a, b) -> {
            try {
                Comparable comA = (Comparable) invokeGet(a, orderTag);
                Comparable comB = (Comparable) invokeGet(b, orderTag);
                return comB.compareTo(comA);
            } catch (Exception e) {
                log.error("分组短链接分页查询排序错误", e);
            }
            return 0;
        });

        return result;
    }

    /**
     * 根据属性名执行实体对象的get方法
     */
    private <T> Object invokeGet(T entity, String propertyName) {
        Object ret = null;
        try {
            String getMethodName = "get" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);
            Method getMethod = entity.getClass().getMethod(getMethodName);
            ret = getMethod.invoke(entity);
        } catch (Exception e) {
            log.error("分组短链接分页查询排序错误", e);
        }
        return ret;
    }

    @Override
    public List<LinkCountVO> listLinkCount(LinkCountDTO linkCountDTO) {
        List<String> gids = linkCountDTO.getGid();
        // select gid, count(*) from t_link_0 where gid in ('PD6dtO') and del_flag = 0 and enable_status = 1 group by gid;
        QueryWrapper<Link> wrapper = new QueryWrapper<>();
        wrapper.select("gid, count(*) as count")
                .lambda()
                .eq(Link::getDelFlag, CommonConstant.NOT_DELETED)
                .eq(Link::getEnableStatus, CommonConstant.HAS_ENABLED)
                .in(Link::getGid, gids)
                .groupBy(Link::getGid);
        List<Map<String, Object>> maps = baseMapper.selectMaps(wrapper);
        return BeanUtil.copyToList(maps, LinkCountVO.class);
    }

    @Override
    @Transactional
    public void updateLink(LinkUpdateDTO linkUpdateDTO) {
        String shortUri = linkUpdateDTO.getShortUri();
        LocalDateTime validDate = linkUpdateDTO.getValidDate();
        // 查询相应短链接
        LambdaQueryWrapper<Link> wrapper = Wrappers.lambdaQuery(Link.class)
                .eq(Link::getGid, linkUpdateDTO.getGid())
                .eq(Link::getShortUri, shortUri)
                .eq(Link::getDelFlag, CommonConstant.NOT_DELETED)
                .eq(Link::getEnableStatus, CommonConstant.HAS_ENABLED);
        Link link = baseMapper.selectOne(wrapper);
        if (link == null) {
            throw new ClientException(BaseErrorCode.LINK_NOT_EXISTS_ERROR);
        }
        // 是否修改了有效期
        boolean changedValidDate = validDate != null && !validDate.equals(link.getValidDate());
        // 封装修改数据
        link.setGid(linkUpdateDTO.getNewGid());
        link.setDescription(linkUpdateDTO.getDescription());
        link.setValidDateType(linkUpdateDTO.getValidDateType());
        link.setOriginUri(linkUpdateDTO.getOriginUri());
        // 让自动填充生效
        link.setUpdateTime(null);
        // 如果传入参数的ValidDateType是自定义有效期，则允许修改有效期
        if (linkUpdateDTO.getValidDateType().equals(ValidDateTypeEnum.CUSTOMIZED.getType())) {
            if (validDate == null) {
                throw new ClientException(BaseErrorCode.LINK_VALID_DATE_ERROR);
            }
            link.setValidDate(validDate);
        } else {
            link.setValidDate(null);
        }

        if (!linkUpdateDTO.getNewGid().equals(linkUpdateDTO.getGid())) {
            // 如果修改了所属分组，则需要先删除该短连接，因为分片键是 gid
            // 修改路由中间表
            LinkGoto linkGoto = LinkGoto.builder().gid(linkUpdateDTO.getNewGid()).shortUri(link.getShortUri()).build();
            linkGotoService.update(linkGoto, Wrappers.lambdaUpdate(LinkGoto.class).eq(LinkGoto::getShortUri, link.getShortUri()));
            // 使用触发器归档删除的记录
            baseMapper.delete(wrapper);
            baseMapper.insert(link);
        } else {
            // 没有修改GID则直接更新
            baseMapper.update(link, wrapper);
        }

        // 如果修改了有效期，则删除缓存
        if (changedValidDate) {
            String key = RedisConstant.GOTO_SHORT_LINK_KEY_PREFIX + shortUri;
            stringRedisTemplate.delete(key);
        }
    }
}
