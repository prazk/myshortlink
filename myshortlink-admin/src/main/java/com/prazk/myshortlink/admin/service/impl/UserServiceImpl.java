package com.prazk.myshortlink.admin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.UUID;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.prazk.myshortlink.admin.common.constant.RedisCacheConstant;
import com.prazk.myshortlink.admin.common.constant.CommonConstant;
import com.prazk.myshortlink.admin.common.context.UserContext;
import com.prazk.myshortlink.admin.common.convention.errorcode.BaseErrorCode;
import com.prazk.myshortlink.admin.common.convention.exception.ClientException;
import com.prazk.myshortlink.admin.mapper.UserMapper;
import com.prazk.myshortlink.admin.pojo.dto.UserLoginDTO;
import com.prazk.myshortlink.admin.pojo.dto.UserLogoutDTO;
import com.prazk.myshortlink.admin.pojo.dto.UserModifyDTO;
import com.prazk.myshortlink.admin.pojo.dto.UserRegisterDTO;
import com.prazk.myshortlink.admin.pojo.entity.User;
import com.prazk.myshortlink.admin.pojo.vo.UserLoginVO;
import com.prazk.myshortlink.admin.pojo.vo.UserVO;
import com.prazk.myshortlink.admin.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final RBloomFilter<String> userRegisterCachePenetrationBloomFilter;
    private final RedissonClient redissonClient;
    private final StringRedisTemplate stringRedisTemplate;

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

    @Override
    public Boolean judgeExistByUsername(String username) {
        return userRegisterCachePenetrationBloomFilter.contains(username);
    }

    @Override
    public void register(UserRegisterDTO userRegisterDTO) {
        // 检测用户名已存在
        String username = userRegisterDTO.getUsername();
        if (this.judgeExistByUsername(username)) {
            throw new ClientException(BaseErrorCode.USER_NAME_EXIST_ERROR);
        }
        // TODO 表单校验

        // 若不存在，则新增用户至数据库：使用了mybatis-plus的字段自动填充功能
        // 首先尝试获取锁
        RLock lock = redissonClient.getLock(RedisCacheConstant.LOCK_USER_REGISTER_PREFIX + username);
        boolean isSuccess = lock.tryLock();

        // 获取失败，返回用户已存在
        if (!isSuccess) {
            throw new ClientException(BaseErrorCode.USER_NAME_EXIST_ERROR);
        }
        // 获取锁成功，进行注册
        try {
            User user = new User();
            BeanUtils.copyProperties(userRegisterDTO, user);
            int insert = baseMapper.insert(user);
            if (insert < 1) {
                // 保存失败
                throw new ClientException(BaseErrorCode.USER_SAVE_ERROR);
            }
            // 添加用户名到布隆过滤器
            userRegisterCachePenetrationBloomFilter.add(user.getUsername());
        } finally { // 锁一定要释放
            lock.unlock();
        }
    }

    @Override
    public void modify(UserModifyDTO userModifyDTO) {
        String username = UserContext.getUser().getUsername();
        if (!username.equals(userModifyDTO.getUsername()))
            throw new ClientException(BaseErrorCode.USER_NAME_VERIFY_ERROR);

        LambdaUpdateWrapper<User> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(User::getUsername, userModifyDTO.getUsername()); // where username = #{dto.getUsername()}
        baseMapper.update(BeanUtil.toBean(userModifyDTO, User.class), wrapper);
    }

    @Override
    public UserLoginVO login(UserLoginDTO userLoginDTO) {
        String username = userLoginDTO.getUsername();
        String key = RedisCacheConstant.TOKEN_USER_LOGIN_PREFIX + username;
        // 用户已登录
        if (stringRedisTemplate.opsForHash().get(key, "token") != null) {
            throw new ClientException(BaseErrorCode.USER_HAS_LOGIN);
        }
        // 查询数据库并校验
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username)
                .eq(User::getPassword, userLoginDTO.getPassword())
                .eq(User::getDelFlag, CommonConstant.NOT_DELETED);
        Long cnt = baseMapper.selectCount(wrapper);

        // TODO 分开校验用户名和密码
        if (cnt == 0)
            throw new ClientException(BaseErrorCode.USER_NOT_EXIST_ERROR);

        // 校验通过，则生成 token，返回给前端，并保存到 Redis
        String token = UUID.fastUUID().toString(true);

        // 保存到 Redis
        // 使用 Redis Hash 结构，key 设计为每个用户唯一，value 中保存 token 和用户信息
        Map<String, String> claims = new HashMap<>();
        claims.put("token", token);
        stringRedisTemplate.opsForHash().putAll(key, claims);
        stringRedisTemplate.expire(key, RedisCacheConstant.DURATION_USER_LOGIN, TimeUnit.MINUTES);

        return UserLoginVO.builder()
                .token(token)
                .username(username)
                .build();
    }

    @Override
    public void logout(UserLogoutDTO userLogoutDTO) {
        String key = RedisCacheConstant.TOKEN_USER_LOGIN_PREFIX + userLogoutDTO.getUsername();
        String token = (String) stringRedisTemplate.opsForHash().get(key, "token");
        if (token != null && token.equals(userLogoutDTO.getToken())) {
            stringRedisTemplate.delete(key);
            return;
        }
        throw new ClientException("用户名或token错误");
    }
}
