package com.bbnc.voice.ThreadLocal;

import com.bbnc.voice.entity.User;

public class ThreadLocalUser {

    private ThreadLocalUser(){
    }

    private static final ThreadLocal<User> USER_INFO_THREAD_LOCAL = new ThreadLocal<>();

    /**
     * 清除用户信息
     */
    public static void clear() {
        USER_INFO_THREAD_LOCAL.remove();
    }

    /**
     * 存储用户信息
     */
    public static void set(User user) {
        USER_INFO_THREAD_LOCAL.set(user);
    }

    /**
     * 获取当前用户信息
     */
    public static User getCurrentUser() {
        return USER_INFO_THREAD_LOCAL.get();
    }

}
