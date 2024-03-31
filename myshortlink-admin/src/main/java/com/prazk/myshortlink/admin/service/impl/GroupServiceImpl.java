package com.prazk.myshortlink.admin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.prazk.myshortlink.admin.common.constant.CommonConstant;
import com.prazk.myshortlink.admin.common.constant.GroupConstant;
import com.prazk.myshortlink.admin.common.context.UserContext;
import com.prazk.myshortlink.admin.common.convention.errorcode.BaseErrorCode;
import com.prazk.myshortlink.admin.common.convention.exception.ClientException;
import com.prazk.myshortlink.admin.mapper.GroupMapper;
import com.prazk.myshortlink.admin.pojo.dto.GroupCreateDTO;
import com.prazk.myshortlink.admin.pojo.dto.GroupSortDTO;
import com.prazk.myshortlink.admin.pojo.dto.GroupUpdateDTO;
import com.prazk.myshortlink.admin.pojo.entity.Group;
import com.prazk.myshortlink.admin.pojo.vo.GroupVO;
import com.prazk.myshortlink.admin.remote.pojo.dto.LinkCountDTO;
import com.prazk.myshortlink.admin.remote.pojo.vo.LinkCountVO;
import com.prazk.myshortlink.admin.remote.service.ShortLinkRemoteService;
import com.prazk.myshortlink.admin.service.GroupService;
import com.prazk.myshortlink.admin.util.GidGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.prazk.myshortlink.admin.common.constant.GroupConstant.GROUP_LIMIT;

@Service
@RequiredArgsConstructor
public class GroupServiceImpl extends ServiceImpl<GroupMapper, Group> implements GroupService {

    private final ShortLinkRemoteService shortLinkRemoteService;

    @Override
    public void saveGroup(GroupCreateDTO groupCreateDTO) {
        String username = UserContext.getUser().getUsername();
        saveGroup(username, groupCreateDTO);
    }

    @Override
    public void saveGroup(String username, GroupCreateDTO groupCreateDTO) {
        // 先查询数据库中当前用户的所有gid
        LambdaQueryWrapper<Group> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Group::getUsername, username);
        List<Group> groups = baseMapper.selectList(wrapper);

        // 分组最大 10个
        if (groups.size() == GROUP_LIMIT)
            throw new ClientException(BaseErrorCode.GROUP_REACH_LIMIT_ERROR);

        // List<Group> ---> Set<String>
        Set<String> gids = groups.stream().map(Group::getGid).collect(Collectors.toSet());

        String gid = GidGenerator.generateUniqueID(gids, GroupConstant.GID_LENGTH);
        Group group = Group.builder()
                .gid(gid)
                .name(groupCreateDTO.getName())
                .username(username)
                .build();

        baseMapper.insert(group);
    }

    @Override
    public List<GroupVO> getGroups() {
        String username = UserContext.getUser().getUsername();

        // 查询用户名对应的分组信息
        LambdaQueryWrapper<Group> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Group::getUsername, username)
                .eq(Group::getDelFlag, CommonConstant.NOT_DELETED);
        List<Group> groups = baseMapper.selectList(wrapper);

        // 查询每个分组下的分组数量
        List<String> gids = groups.stream().map(Group::getGid).toList();

        // 调用中台查询用户的所有分组的短链接数量接口
        List<LinkCountVO> list = shortLinkRemoteService
                .listLinkCount(new LinkCountDTO(gids)).getData().stream().toList();

        List<GroupVO> result = BeanUtil.copyToList(groups, GroupVO.class);
        result.forEach(groupVO -> {
            String gid = groupVO.getGid();
            list.forEach(linkCountVO -> {
                if (linkCountVO.getGid().equals(gid)) {
                    groupVO.setCount(linkCountVO.getCount());
                }
            });
        });

        return result;
    }

    @Override
    public void updateGroup(GroupUpdateDTO groupUpdateDTO) {
        // 根据用户名和分组ID，修改分组名
        LambdaUpdateWrapper<Group> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Group::getUsername, UserContext.getUser().getUsername())
                .eq(Group::getGid, groupUpdateDTO.getGid())
                .eq(Group::getDelFlag, CommonConstant.NOT_DELETED);

        Group group = Group.builder()
                .name(groupUpdateDTO.getName())
                .build();
        int update = baseMapper.update(group, wrapper);
        if (update == 0)
            throw new ClientException(BaseErrorCode.GROUP_NOT_FOUND_ERROR);
    }

    @Override
    public void deleteGroup(String gid) {
        // 根据用户名和分组ID，删除分组名
        LambdaUpdateWrapper<Group> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Group::getUsername, UserContext.getUser().getUsername())
                .eq(Group::getGid, gid)
                .eq(Group::getDelFlag, CommonConstant.NOT_DELETED);

        Group group = Group.builder()
                .delFlag(CommonConstant.HAS_BEEN_DELETED)
                .build();
        int update = baseMapper.update(group, wrapper);
        if (update == 0)
            throw new ClientException(BaseErrorCode.GROUP_NOT_FOUND_ERROR);
    }

    @Override
    @Transactional
    public void sortGroup(List<GroupSortDTO> list) {
        // 保存传入的排序分组
        for (GroupSortDTO groupSortDTO : list) {
            LambdaUpdateWrapper<Group> wrapper = new LambdaUpdateWrapper<>();
            wrapper.eq(Group::getUsername, UserContext.getUser().getUsername())
                    .eq(Group::getGid, groupSortDTO.getGid())
                    .eq(Group::getDelFlag, CommonConstant.NOT_DELETED);

            Group group = Group.builder()
                    .sortOrder(groupSortDTO.getSortOrder())
                    .build();
            int update = baseMapper.update(group, wrapper);
            if (update == 0)
                throw new ClientException(BaseErrorCode.GROUP_NOT_FOUND_ERROR);
        }
    }
}
