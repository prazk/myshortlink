package com.prazk.myshortlink.admin.controller;

import com.prazk.myshortlink.admin.common.convention.result.Result;
import com.prazk.myshortlink.admin.common.convention.result.Results;
import com.prazk.myshortlink.admin.pojo.vo.UserVO;
import com.prazk.myshortlink.admin.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/user")
public class UserController {
    @Autowired
    private UserService userService;
    /**
     * 根据用户名查询信息
     */
    @GetMapping("/{username}")
    public Result<UserVO> getByUsername(@PathVariable String username) {
        UserVO userVO = userService.getByUsername(username);

        return Results.success(userVO);
    }
}
