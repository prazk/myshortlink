package com.prazk.myshortlink.project.biz.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class LinkNotFoundController {

    /**
     * 短链接不存在
     */
    @RequestMapping("/link/notfound")
    public String notfound() {
        return "notfound";
    }
}
