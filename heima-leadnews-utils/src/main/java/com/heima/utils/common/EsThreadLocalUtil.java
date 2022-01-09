package com.heima.utils.common;

import com.heima.model.user.pojos.ApUser;
import com.heima.model.wemedia.pojos.WmUser;

public class EsThreadLocalUtil {

    private final static ThreadLocal<ApUser> ES_USER_THREAD_LOCAL = new ThreadLocal<>();

    /**
     * 添加用户
     *
     * @param apUser
     */
    public static void setUser(ApUser apUser) {
        ES_USER_THREAD_LOCAL.set(apUser);
    }

    /**
     * 获取用户
     */
    public static ApUser getUser() {
        return ES_USER_THREAD_LOCAL.get();
    }

    /**
     * 清理用户
     */
    public static void clear() {
        ES_USER_THREAD_LOCAL.remove();
    }
}