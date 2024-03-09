package com.prazk.myshortlink.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.prazk.myshortlink.admin.mapper.UserMapper;
import com.prazk.myshortlink.admin.pojo.entity.User;
import com.prazk.myshortlink.admin.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

}
