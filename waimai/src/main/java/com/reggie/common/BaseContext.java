package com.reggie.common;

/**
 * 基于ThreadLocal封装工具类，保存，获取当前用户id
 *
 */

public class BaseContext {
    private static ThreadLocal<Long> threadLocal=new ThreadLocal<>();
    public static void setCurrentId(Long id){
        threadLocal.set(id);
    }
    public static Long getCurrent(){
        return threadLocal.get();
    }
}
