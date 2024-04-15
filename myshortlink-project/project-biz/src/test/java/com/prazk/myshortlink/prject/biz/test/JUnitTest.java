package com.prazk.myshortlink.prject.biz.test;


import com.prazk.myshortlink.project.biz.util.LinkUtil;
import org.junit.jupiter.api.Test;

public class JUnitTest {
    @Test
    public void test() {
        String url = "https://www.zhihu.com/";
        String title = LinkUtil.getTitleByUrl(url);
        System.out.println(title);
    }
}
