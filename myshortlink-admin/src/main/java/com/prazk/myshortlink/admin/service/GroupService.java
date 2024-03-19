package com.prazk.myshortlink.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.prazk.myshortlink.admin.pojo.dto.GroupCreateDTO;
import com.prazk.myshortlink.admin.pojo.entity.Group;
import com.prazk.myshortlink.admin.pojo.vo.GroupVO;

import java.util.List;


public interface GroupService extends IService<Group> {

    void saveGroup(GroupCreateDTO groupCreateDTO);

    List<GroupVO> getGroups();
}
