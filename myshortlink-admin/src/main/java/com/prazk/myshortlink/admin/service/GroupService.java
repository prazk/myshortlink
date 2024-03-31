package com.prazk.myshortlink.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.prazk.myshortlink.admin.pojo.dto.GroupCreateDTO;
import com.prazk.myshortlink.admin.pojo.dto.GroupSortDTO;
import com.prazk.myshortlink.admin.pojo.dto.GroupUpdateDTO;
import com.prazk.myshortlink.admin.pojo.entity.Group;
import com.prazk.myshortlink.admin.pojo.vo.GroupVO;

import java.util.List;


public interface GroupService extends IService<Group> {

    void saveGroup(GroupCreateDTO groupCreateDTO);
    void saveGroup(String username, GroupCreateDTO groupCreateDTO);

    List<GroupVO> getGroups();

    void updateGroup(GroupUpdateDTO groupUpdateDTO);

    void deleteGroup(String gid);

    void sortGroup(List<GroupSortDTO> list);
}
