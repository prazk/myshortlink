package com.prazk.myshortlink.admin.controller;

import com.prazk.myshortlink.admin.pojo.entity.User;
import com.prazk.myshortlink.admin.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
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
    public User getUserByUsername(String username) {
        return userService.getById(1);
    }
}
