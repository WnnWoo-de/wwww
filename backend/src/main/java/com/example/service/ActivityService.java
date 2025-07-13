package com.example.service;

import com.example.mapper.ActivityMapper;
import com.example.mapper.RegistrationMapper;
import com.example.model.Activity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ActivityService {
    
    @Autowired
    private ActivityMapper activityMapper;
    
    @Autowired
    private RegistrationMapper registrationMapper;
    
    // 获取所有活动
    public List<ActivityDTO> getAllActivities() {
        return activityMapper.findAll().stream().map(this::convertToDto).collect(Collectors.toList());
    }
    
    // 根据ID获取活动
    public ActivityDTO getActivityById(Integer id) {
        Activity activity = activityMapper.findById(id);
        return activity != null ? convertToDto(activity) : null;
    }
    
    // 根据状态获取活动
    public List<Activity> getActivitiesByStatus(String status) {
        return activityMapper.findByStatus(status);
    }
    
    // 根据分类获取活动
    public List<Activity> getActivitiesByCategory(String category) {
        return activityMapper.findByCategory(category);
    }
    
    // 根据标题搜索活动
    public List<Activity> searchActivitiesByTitle(String title) {
        return activityMapper.findByTitleContaining(title);
    }
    
    // 创建新活动
    public boolean createActivity(Activity activity) {
        // 设置默认值
        if (activity.getCurrentParticipants() == null) {
            activity.setCurrentParticipants(0);
        }
        if (activity.getStatus() == null || activity.getStatus().isEmpty()) {
            activity.setStatus("active");
        }
        
        return activityMapper.insert(activity) > 0;
    }
    
    // 更新活动信息
    public boolean updateActivity(Activity activity) {
        return activityMapper.update(activity) > 0;
    }
    
    // 删除活动
    public boolean deleteActivity(Integer id) {
        return activityMapper.deleteById(id) > 0;
    }
    
    // 更新活动状态
    public boolean updateActivityStatus(Integer id, String status) {
        Activity activity = activityMapper.findById(id);
        if (activity != null) {
            activity.setStatus(status);
            return activityMapper.update(activity) > 0;
        }
        return false;
    }
    
    // 检查活动是否可以报名
    public boolean canRegister(Integer activityId) {
        Activity activity = activityMapper.findById(activityId);
        if (activity == null || !"active".equals(activity.getStatus())) {
            return false;
        }
        
        // 检查是否还有名额
        return activity.getCurrentParticipants() < activity.getMaxParticipants();
    }
    
    // 增加参与人数
    public void incrementParticipants(Integer activityId) {
        if (!canRegister(activityId)) {
            throw new IllegalStateException("活动已满员或不可报名。");
        }
        int updatedRows = activityMapper.incrementParticipants(activityId);
        if (updatedRows == 0) {
            throw new RuntimeException("增加参与人数失败，可能存在并发冲突。");
        }
    }
    
    // 减少参与人数
    public void decrementParticipants(Integer activityId) {
        int updatedRows = activityMapper.decrementParticipants(activityId);
        if (updatedRows == 0) {
            throw new RuntimeException("减少参与人数失败，可能存在并发冲突。");
        }
    }
    
    // 同步参与人数（根据实际报名记录更新）
    public boolean syncParticipantCount(Integer activityId) {
        int actualCount = registrationMapper.getRegisteredCountByActivityId(activityId.longValue());
        return activityMapper.updateCurrentParticipants(activityId, actualCount) > 0;
    }
    
    // 分页获取活动
    public List<Activity> getActivitiesWithPagination(int page, int size) {
        int offset = (page - 1) * size;
        return activityMapper.findWithPagination(offset, size);
    }
    
    // 获取活动总数
    public Page<Activity> getActivitiesByPage(int page, int size) {
        int offset = (page - 1) * size;
        List<Activity> activities = activityMapper.findWithPagination(offset, size);
        int total = activityMapper.getTotalCount();
        return new PageImpl<>(activities, PageRequest.of(page - 1, size), total);
    }

    public int getTotalActivityCount() {
        return activityMapper.getTotalCount();
    }
    
    // 根据状态获取活动数量
    public int getActivityCountByStatus(String status) {
        return activityMapper.getCountByStatus(status);
    }
    
    // 获取即将开始的活动
    public List<Activity> getUpcomingActivities(int limit) {
        return activityMapper.findUpcomingActivities(limit);
    }
    
    // 获取活动统计信息
    public ActivityStats getActivityStats() {
        ActivityStats stats = new ActivityStats();
        stats.setTotalActivities(activityMapper.getTotalCount());
        stats.setActiveActivities(activityMapper.getCountByStatus("active"));
        stats.setCompletedActivities(activityMapper.getCountByStatus("completed"));
        stats.setCancelledActivities(activityMapper.getCountByStatus("cancelled"));
        return stats;
    }
    
    // 内部类：活动统计信息
    public static class ActivityStats {
        private int totalActivities;
        private int activeActivities;
        private int completedActivities;
        private int cancelledActivities;
        
        // Getter和Setter方法
        public int getTotalActivities() {
            return totalActivities;
        }
        
        public void setTotalActivities(int totalActivities) {
            this.totalActivities = totalActivities;
        }
        
        public int getActiveActivities() {
            return activeActivities;
        }
        
        public void setActiveActivities(int activeActivities) {
            this.activeActivities = activeActivities;
        }
        
        public int getCompletedActivities() {
            return completedActivities;
        }
        
        public void setCompletedActivities(int completedActivities) {
            this.completedActivities = completedActivities;
        }
        
        public int getCancelledActivities() {
            return cancelledActivities;
        }
        
        public void setCancelledActivities(int cancelledActivities) {
            this.cancelledActivities = cancelledActivities;
        }
    }

    private ActivityDTO convertToDto(Activity activity) {
        ActivityDTO activityDTO = new ActivityDTO();
        activityDTO.setId(activity.getId().intValue());
        activityDTO.setTitle(activity.getTitle());
        activityDTO.setDescription(activity.getDescription());
        activityDTO.setStartTime(activity.getStartTime());
        activityDTO.setEndTime(activity.getEndTime());
        activityDTO.setLocation(activity.getLocation());
        activityDTO.setRequiredVolunteers(activity.getMaxParticipants());
        activityDTO.setMaxParticipants(activity.getMaxParticipants());
        activityDTO.setCurrentParticipants(activity.getCurrentParticipants());
        activityDTO.setStatus(activity.getStatus());
        activityDTO.setCategory(activity.getCategory());
        return activityDTO;
    }
}