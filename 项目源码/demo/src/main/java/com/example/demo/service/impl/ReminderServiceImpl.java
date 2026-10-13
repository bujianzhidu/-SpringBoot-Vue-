package com.example.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.entity.Reminder;
import com.example.demo.mapper.ReminderMapper;
import com.example.demo.service.ReminderService;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReminderServiceImpl extends ServiceImpl<ReminderMapper, Reminder> implements ReminderService {
    
    @Override
    public List<Reminder> getPendingReminders() {
        LambdaQueryWrapper<Reminder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Reminder::getStatus, "PENDING")
               .le(Reminder::getRemindTime, LocalDateTime.now())
               .orderByAsc(Reminder::getRemindTime);
        return list(wrapper);
    }
    
    @Override
    public void completeReminder(Long id) {
        Reminder reminder = getById(id);
        if (reminder != null) {
            reminder.setStatus("COMPLETED");
            updateById(reminder);
            
            // 如果是重复提醒，设置下次提醒时间
            if (reminder.getRepeatType() > 0) {
                LocalDateTime nextTime = calculateNextRemindTime(reminder);
                if (nextTime != null) {
                    Reminder nextReminder = new Reminder();
                    nextReminder.setTitle(reminder.getTitle());
                    nextReminder.setContent(reminder.getContent());
                    nextReminder.setRemindTime(nextTime);
                    nextReminder.setType(reminder.getType());
                    nextReminder.setStatus("PENDING");
                    nextReminder.setRepeatType(reminder.getRepeatType());
                    save(nextReminder);
                }
            }
        }
    }
    
    @Override
    public void cancelReminder(Long id) {
        Reminder reminder = getById(id);
        if (reminder != null) {
            reminder.setStatus("CANCELLED");
            updateById(reminder);
        }
    }
    
    private LocalDateTime calculateNextRemindTime(Reminder reminder) {
        LocalDateTime currentTime = reminder.getRemindTime();
        switch (reminder.getRepeatType()) {
            case 1: // 每天
                return currentTime.plusDays(1);
            case 2: // 每周
                return currentTime.plusWeeks(1);
            case 3: // 每月
                return currentTime.plusMonths(1);
            default:
                return null;
        }
    }
} 