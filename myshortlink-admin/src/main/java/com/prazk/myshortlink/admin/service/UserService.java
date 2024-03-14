package com.prazk.myshortlink.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.prazk.myshortlink.admin.pojo.dto.UserLoginDTO;
import com.prazk.myshortlink.admin.pojo.dto.UserLogoutDTO;
import com.prazk.myshortlink.admin.pojo.dto.UserModifyDTO;
import com.prazk.myshortlink.admin.pojo.dto.UserRegisterDTO;
import com.prazk.myshortlink.admin.pojo.entity.User;
import com.prazk.myshortlink.admin.pojo.vo.UserLoginVO;
import com.prazk.myshortlink.admin.pojo.vo.UserVO;

public interface UserService extends IService<User> {

    UserVO getByUsername(String username);

    Boolean judgeExistByUsername(String username);

    void register(UserRegisterDTO userRegisterDTO);

    void modify(UserModifyDTO userModifyDTO);

    UserLoginVO login(UserLoginDTO userLoginDTO);

    void logout(UserLogoutDTO userLogoutDTO);
}
