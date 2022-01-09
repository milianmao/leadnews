package com.heima.schedule.service;

import com.heima.model.schedule.dtos.Task;

/**
 * @Author milian
 * @Description
 * @Date 2021/11/24 0024 16:14
 * @Version 1.0
 */
public interface TaskService {
    /**
     * @return long
     * @Author milian
     * @Description 添加任务
     * @Date 16:16
     * @Param [task]
     **/
    public long addTask(Task task);

    /**
     * @return boolean
     * @Author milian
     * @Description 取消任务
     * @Date 16:43
     * @Param [taskId]
     **/
    public boolean cancelTask(long taskId);

    /**
     * @return com.heima.model.schedule.dtos.Task
     * @Author milian
     * @Description 按照类型和优先级拉取任务
     * @Date 16:57
     * @Param [type, priority]
     **/
    public Task poll(int type, int priority);

    /**
     * @return void
     * @Author milian
     * @Description 定时刷新未来任务
     * @Date 18:11
     * @Param []
     **/
    public void refresh();
}
