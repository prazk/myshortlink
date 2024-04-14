package com.prazk.myshortlink.admin.controller;

import com.prazk.myshortlink.admin.pojo.dto.UserLoginDTO;
import com.prazk.myshortlink.admin.pojo.dto.UserLogoutDTO;
import com.prazk.myshortlink.admin.pojo.dto.UserModifyDTO;
import com.prazk.myshortlink.admin.pojo.dto.UserRegisterDTO;
import com.prazk.myshortlink.admin.pojo.vo.UserLoginVO;
import com.prazk.myshortlink.admin.pojo.vo.UserVO;
import com.prazk.myshortlink.admin.service.UserService;
import com.prazk.myshortlink.common.convention.result.Result;
import com.prazk.myshortlink.common.convention.result.Results;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("short-link/admin/user")
@RequiredArgsConstructor // 生成一个包含final和标识了@NotNull的变量的构造方法
public class UserController {

    private final UserService userService;


    /**
     * 判断用户名是否存在接口
     * 存在：返回true
     * 不存在，返回false
     */
    @GetMapping("/has-username")
    public Result<Boolean> judgeExistByUsername(@RequestParam String username) {
        return Results.success(userService.judgeExistByUsername(username));
    }

    /**
     * 根据用户名查询信息
     */
    @GetMapping("/{username}")
    public Result<UserVO> getByUsername(@PathVariable String username) {
        UserVO userVO = userService.getByUsername(username);

        return Results.success(userVO);
    }

    /**
     * 用户注册接口
     */
    @PostMapping
    public Result<Void> register(@RequestBody UserRegisterDTO userRegisterDTO) {
        userService.register(userRegisterDTO);
        return Results.success();
    }

    /**
     * 修改用户
     */
    @PutMapping
    public Result<Void> modify(@RequestBody UserModifyDTO userModifyDTO) {
        userService.modify(userModifyDTO);
        return Results.success();
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<UserLoginVO> login(@RequestBody UserLoginDTO userLoginDTO) {
        UserLoginVO loginVO = userService.login(userLoginDTO);
        return Results.success(loginVO);
    }

    /**
     * 退出登录
     */
    @DeleteMapping("/logout")
    public Result<Void> logout(@RequestBody UserLogoutDTO userLogoutDTO) {
        userService.logout(userLogoutDTO);
        return Results.success();
    }
}
