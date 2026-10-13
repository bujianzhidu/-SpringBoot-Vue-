package com.example.demo.controller;

import com.example.demo.entity.Reminder;
import com.example.demo.service.ReminderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/reminders")
@Tag(name = "提醒管理", description = "提醒相关的接口")
public class ReminderController {
    
    @Autowired
    private ReminderService reminderService;
    
    @GetMapping
    @Operation(summary = "获取待处理的提醒列表")
    public List<Reminder> getPendingReminders() {
        return reminderService.getPendingReminders();
    }
    
    @PostMapping("/{id}/complete")
    @Operation(summary = "完成提醒")
    public void completeReminder(@PathVariable Long id) {
        reminderService.completeReminder(id);
    }
    
    @PostMapping("/{id}/cancel")
    @Operation(summary = "取消提醒")
    public void cancelReminder(@PathVariable Long id) {
        reminderService.cancelReminder(id);
    }
    
    @PostMapping
    @Operation(summary = "创建提醒")
    public boolean createReminder(@RequestBody Reminder reminder) {
        return reminderService.save(reminder);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "更新提醒")
    public boolean updateReminder(@PathVariable Long id, @RequestBody Reminder reminder) {
        reminder.setId(id);
        return reminderService.updateById(reminder);
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "删除提醒")
    public boolean deleteReminder(@PathVariable Long id) {
        return reminderService.removeById(id);
    }
} 