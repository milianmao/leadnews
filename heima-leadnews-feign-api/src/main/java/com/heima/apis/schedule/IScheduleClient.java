package com.heima.apis.schedule;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.schedule.dtos.Task;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @Author milian
 * @Description
 * @Date 2021/11/24 0024 20:31
 * @Version 1.0
 */
@FeignClient(value = "leadnews-schedule")
public interface IScheduleClient {

    /**
     * @return long
     * @Author milian
     * @Description 添加任务
     * @Date 16:16
     * @Param [task]
     **/
    @PostMapping("/api/v1/task/add")
    public ResponseResult addTask(@RequestBody Task task);

    /**
     * @return boolean
     * @Author milian
     * @Description 取消任务
     * @Date 16:43
     * @Param [taskId]
     **/
    @GetMapping("/api/v1/task/cancel/{taskId}")
    public ResponseResult cancelTask(@PathVariable("taskId") long taskId);

    /**
     * @return com.heima.model.schedule.dtos.Task
     * @Author milian
     * @Description 按照类型和优先级拉取任务
     * @Date 16:57
     * @Param [type, priority]
     **/
    @GetMapping("/api/v1/task/poll/{type}/{priority}")
    public ResponseResult poll(@PathVariable("type") int type, @PathVariable("priority") int priority);
}
