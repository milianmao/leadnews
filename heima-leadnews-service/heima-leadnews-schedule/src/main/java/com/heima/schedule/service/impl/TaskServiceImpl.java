package com.heima.schedule.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.heima.common.constants.ScheduleConstants;
import com.heima.common.schedule.CacheService;
import com.heima.model.schedule.dtos.Task;
import com.heima.model.schedule.pojos.Taskinfo;
import com.heima.model.schedule.pojos.TaskinfoLogs;
import com.heima.schedule.mapper.TaskinfoLogsMapper;
import com.heima.schedule.mapper.TaskinfoMapper;
import com.heima.schedule.service.TaskService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * @Author milian
 * @Description
 * @Date 2021/11/24 0024 16:15
 * @Version 1.0
 */
@Service
@Transactional
@Slf4j
public class TaskServiceImpl implements TaskService {
    @Autowired
    private TaskinfoMapper taskinfoMapper;
    @Autowired
    private TaskinfoLogsMapper taskinfoLogsMapper;
    @Autowired
    private CacheService cacheService;

    /**
     * 添加任务
     */
    @Override
    public long addTask(Task task) {
        // 添加任务到数据库并保存日志
        Boolean result = addTaskToDb(task);
        if (result) {
            // 添加任务到redis
            addTaskToCache(task);
        }
        return task.getTaskId();
    }

    /**
     * 取消任务
     */
    @Override
    public boolean cancelTask(long taskId) {
        boolean flag = false;
        // 删除数据库中的任务并保存日志
        Task task = updateDb(taskId, ScheduleConstants.EXECUTED);
        if (task != null) {
            // 删除redis的任务
            deleteTaskToCache(task);
            flag = true;
        }

        return flag;
    }

    /**
     * 按照类型和优先级拉取任务
     */
    @Override
    public Task poll(int type, int priority) {
        Task task = null;
        try {
            String key = type + "_" + priority;
            String taskJSON = cacheService.lLeftPop(ScheduleConstants.TOPIC + key);
            if (StringUtils.isNotBlank(taskJSON)) {
                task = JSON.parseObject(taskJSON, Task.class);
                // 更新数据库
                updateDb(task.getTaskId(), ScheduleConstants.EXECUTED);

            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("poll task exception");
        }
        return task;
    }

    @Scheduled(cron = "0 */1 * * * ?")
    @Override
    public void refresh() {
        String token = cacheService.tryLock("FUTURE_TASK_SYNC", 1000 * 30);
        if (StringUtils.isNoneEmpty(token)) {
            System.out.println(System.currentTimeMillis() / 1000 + "执行了定时任务");
            // 获取未来任务key
            Set<String> futureKeys = cacheService.scan(ScheduleConstants.FUTURE + "*");
            for (String futureKey : futureKeys) {
                String topicKey = ScheduleConstants.TOPIC + futureKey.split(ScheduleConstants.FUTURE)[1];
                //获取消费任务
                Set<String> tasks = cacheService.zRangeByScore(futureKey, 0, System.currentTimeMillis());
                if (!tasks.isEmpty()) {
                    cacheService.refreshWithPipeline(futureKey, topicKey, tasks);
                }
                System.out.println("成功的将" + futureKey + "下的当前需要执行的任务数据刷新到" + topicKey + "下");
            }
        }
    }

    /**
     * 数据库任务同步到redis中
     */
    @Scheduled(cron = "0 */5 * * * ?")
    @PostConstruct
    public void reloadData() {
        clearCache();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, 5);
        // 查询小于未来五分钟的所有任务
        List<Taskinfo> taskinfos = taskinfoMapper.selectList(Wrappers.<Taskinfo>lambdaQuery()
                .lt(Taskinfo::getExecuteTime, calendar.getTime()));
        if (taskinfos != null && taskinfos.size() > 0) {
            for (Taskinfo taskinfo : taskinfos) {
                Task task = new Task();
                BeanUtils.copyProperties(taskinfo, task);
                task.setExecuteTime(taskinfo.getExecuteTime().getTime());
                addTaskToCache(task);
            }
        }
    }

    /**
     * @return void
     * @Author milian
     * @Description //TODO 清空redis缓存任务
     * @Date 20:21
     * @Param []
     **/
    private void clearCache() {
        Set<String> futureTask = cacheService.scan(ScheduleConstants.FUTURE + "*");
        Set<String> topicTask = cacheService.scan(ScheduleConstants.TOPIC + "*");
        cacheService.delete(futureTask);
        cacheService.delete(topicTask);
    }


    /**
     * @return void
     * @Author milian
     * @Description //TODO 删除redis缓存中的任务
     * @Date 16:52
     * @Param [task]
     **/
    private void deleteTaskToCache(Task task) {
        String key = task.getTaskType() + "_" + task.getPriority();
        if (task.getExecuteTime() <= System.currentTimeMillis()) {
            cacheService.lRemove(ScheduleConstants.TOPIC + key, 0, JSON.toJSONString(task));
        } else {
            cacheService.zRemove(ScheduleConstants.FUTURE + key, JSON.toJSONString(task));
        }
    }

    /**
     * @return void
     * @Author milian
     * @Description //TODO 删除数据库中的任务并修改日志
     * @Date 16:45
     * @Param [taskId]
     **/
    private Task updateDb(long taskId, int status) {
        Task task = null;
        try {
            // 删除任务
            taskinfoMapper.deleteById(taskId);
            // 修改任务日志
            TaskinfoLogs taskinfoLogs = taskinfoLogsMapper.selectById(taskId);
            taskinfoLogs.setStatus(status);
            taskinfoLogsMapper.updateById(taskinfoLogs);
            task = new Task();
            BeanUtils.copyProperties(taskinfoLogs, task);
            task.setExecuteTime(taskinfoLogs.getExecuteTime().getTime());
        } catch (Exception e) {
            log.error("task cancel exception taskid={}", taskId);
        }
        return task;
    }

    /**
     * @return void
     * @Author milian
     * @Description //TODO 添加任务到redis中
     * @Date 16:28
     * @Param [task]
     **/
    private void addTaskToCache(Task task) {
        String key = task.getTaskType() + "_" + task.getPriority();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, 5);
        long nextScheduleTime = calendar.getTimeInMillis();
        if (task.getExecuteTime() <= System.currentTimeMillis()) {
            // 如果任务的执行时间小于等于当前时间，存入list
            cacheService.lLeftPush(ScheduleConstants.TOPIC + key, JSON.toJSONString(task));
        } else if (task.getExecuteTime() <= nextScheduleTime) {
            // 如果任务的执行时间大于当前时间 && 小于等于预设时间（未来5分钟） 存入zset中
            cacheService.zAdd(ScheduleConstants.FUTURE, JSON.toJSONString(task), task.getExecuteTime());
        }
    }

    /**
     * @return void
     * @Author milian
     * @Description //TODO 添加任务到数据库
     * @Date 16:18
     * @Param [task]
     **/
    private Boolean addTaskToDb(Task task) {
        boolean flag = false;
        if (task == null) {
            return false;
        }
        try {
            // 保存任务
            Taskinfo taskinfo = new Taskinfo();
            BeanUtils.copyProperties(task, taskinfo);
            taskinfo.setExecuteTime(new Date(task.getExecuteTime()));
            taskinfoMapper.insert(taskinfo);
            task.setTaskId(taskinfo.getTaskId());
            // 保存日志
            TaskinfoLogs taskinfoLogs = new TaskinfoLogs();
            BeanUtils.copyProperties(taskinfo, taskinfoLogs);
            taskinfoLogs.setStatus(ScheduleConstants.SCHEDULED);
            taskinfoLogs.setVersion(0);
            taskinfoLogsMapper.insert(taskinfoLogs);
            flag = true;
        } catch (BeansException e) {
            e.printStackTrace();
        }
        return flag;
    }
}
