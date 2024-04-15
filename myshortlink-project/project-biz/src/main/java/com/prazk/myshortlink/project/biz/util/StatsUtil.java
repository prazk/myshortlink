package com.prazk.myshortlink.project.biz.util;

public class StatsUtil {
    /**
     * 处理操作系统名称
     */
    public static String dealOsName(String os) {
        if (os.contains("Windows")) {
            return "Windows";
        } else if (os.contains("OSX")) {
            return "Mac OS";
        } else if (os.contains("iPhone") || os.contains("iPad")) {
            return "iOS";
        }
        return os;
    }
}
