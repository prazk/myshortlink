package com.prazk.myshortlink.admin.common.context;

import com.prazk.myshortlink.admin.pojo.entity.User;

public class UserContext {
    public static ThreadLocal<User> threadLocal = new ThreadLocal<>();

    public static void setUser(User user) {
        threadLocal.set(user);
    }

    public static User getUser() {
        return threadLocal.get();
    }

    public static void removeUser() {
        threadLocal.remove();
    }
}
