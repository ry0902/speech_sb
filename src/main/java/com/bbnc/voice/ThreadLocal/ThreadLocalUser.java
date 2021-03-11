package com.bbnc.voice.ThreadLocal;

import com.bbnc.voice.entity.SysUser;

public class ThreadLocalUser {

    private ThreadLocalUser(){
    }

    private static final ThreadLocal<SysUser> USER_INFO_THREAD_LOCAL = new ThreadLocal<>();

    /**
     * 清除用户信息
     */
    public static void clear() {
        USER_INFO_THREAD_LOCAL.remove();
    }

    /**
     * 存储用户信息
     */
    public static void set(SysUser user) {
        USER_INFO_THREAD_LOCAL.set(user);
    }

    /**
     * 获取当前用户信息
     */
    public static SysUser getCurrentUser() {
        return USER_INFO_THREAD_LOCAL.get();
    }

}
