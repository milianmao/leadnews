package com.heima.wemedia.service;

import java.util.Date;

public interface WmNewsTaskService {
    /**
     * 添加任务到延迟队列中
     */
    public void addNewsToTask(Integer id, Date publishTime);

    /**
     * 消费延迟队列
     */
    public void scanNewsByTask();
}