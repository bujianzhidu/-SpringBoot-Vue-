package com.example.demo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.demo.entity.Reminder;
import java.util.List;

public interface ReminderService extends IService<Reminder> {
    /**
     * 获取待处理的提醒列表
     * @return 提醒列表
     */
    List<Reminder> getPendingReminders();
    
    /**
     * 完成提醒
     * @param id 提醒ID
     */
    void completeReminder(Long id);
    
    /**
     * 取消提醒
     * @param id 提醒ID
     */
    void cancelReminder(Long id);
} 