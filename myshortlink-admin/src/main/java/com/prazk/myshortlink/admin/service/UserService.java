package com.prazk.myshortlink.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.prazk.myshortlink.admin.pojo.dto.UserModifyDTO;
import com.prazk.myshortlink.admin.pojo.dto.UserRegisterDTO;
import com.prazk.myshortlink.admin.pojo.entity.User;
import com.prazk.myshortlink.admin.pojo.vo.UserVO;

public interface UserService extends IService<User> {

    UserVO getByUsername(String username);

    Boolean judgeExistByUsername(String username);

    void register(UserRegisterDTO userRegisterDTO);

    void modify(UserModifyDTO userModifyDTO);
}
