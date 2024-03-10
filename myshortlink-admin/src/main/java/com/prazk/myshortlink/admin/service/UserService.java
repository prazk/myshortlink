package com.prazk.myshortlink.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.prazk.myshortlink.admin.pojo.entity.User;
import com.prazk.myshortlink.admin.pojo.vo.UserVO;

public interface UserService extends IService<User> {

    UserVO getByUsername(String username);
}
