package com.prazk.myshortlink.admin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.prazk.myshortlink.admin.common.constant.CommonConstant;
import com.prazk.myshortlink.admin.common.constant.GroupConstant;
import com.prazk.myshortlink.admin.mapper.GroupMapper;
import com.prazk.myshortlink.admin.pojo.dto.GroupCreateDTO;
import com.prazk.myshortlink.admin.pojo.entity.Group;
import com.prazk.myshortlink.admin.pojo.vo.GroupVO;
import com.prazk.myshortlink.admin.service.GroupService;
import com.prazk.myshortlink.admin.util.GidGenerator;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class GroupServiceImpl extends ServiceImpl<GroupMapper, Group> implements GroupService {

    @Override
    public void saveGroup(GroupCreateDTO groupCreateDTO) {
        // TODO 获取用户名
        String username = "zjx";

        // 先查询数据库中当前用户的所有gid
        LambdaQueryWrapper<Group> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Group::getUsername, username);
        List<Group> groups = baseMapper.selectList(wrapper);

        // TODO 分组最大 10个


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
        // TODO 获取用户名
        String username = "zjx";

        LambdaQueryWrapper<Group> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Group::getUsername, username)
                .eq(Group::getDelFlag, CommonConstant.NOT_DELETED);
        List<Group> groups = baseMapper.selectList(wrapper);

        return BeanUtil.copyToList(groups, GroupVO.class);
    }
}
