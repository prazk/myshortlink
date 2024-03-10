package com.prazk.myshortlink.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.prazk.myshortlink.admin.common.convention.errorcode.BaseErrorCode;
import com.prazk.myshortlink.admin.common.convention.exception.ClientException;
import com.prazk.myshortlink.admin.mapper.UserMapper;
import com.prazk.myshortlink.admin.pojo.entity.User;
import com.prazk.myshortlink.admin.pojo.vo.UserVO;
import com.prazk.myshortlink.admin.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Override
    public UserVO getByUsername(String username) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("username", username);
        User user = baseMapper.selectOne(wrapper);
        if (user == null)
            throw new ClientException(BaseErrorCode.USER_NOT_EXIST_ERROR);
        // 封装为VO对象
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);

        return userVO;
    }
}
