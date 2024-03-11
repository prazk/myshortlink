package com.prazk.myshortlink.admin.controller;

import com.prazk.myshortlink.admin.common.convention.result.Result;
import com.prazk.myshortlink.admin.common.convention.result.Results;
import com.prazk.myshortlink.admin.pojo.dto.UserRegisterDTO;
import com.prazk.myshortlink.admin.pojo.vo.UserVO;
import com.prazk.myshortlink.admin.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/user")
@RequiredArgsConstructor // 生成一个包含final和标识了@NotNull的变量的构造方法
public class UserController {

    private final UserService userService;

    /**
     * 根据用户名查询信息
     */
    @GetMapping("/{username}")
    public Result<UserVO> getByUsername(@PathVariable String username) {
        UserVO userVO = userService.getByUsername(username);

        return Results.success(userVO);
    }
    /**
     * 判断用户名是否存在接口
     */
    @GetMapping("/has/{username}")
    public Result<Boolean> judgeExistByUsername(@PathVariable String username) {
        return Results.success(userService.judgeExistByUsername(username));
    }

    /**
     * 用户注册接口
     */
    @PostMapping
    public Result<Void> register(@RequestBody UserRegisterDTO userRegisterDTO) {
        userService.register(userRegisterDTO);
        return Results.success();
    }
}
