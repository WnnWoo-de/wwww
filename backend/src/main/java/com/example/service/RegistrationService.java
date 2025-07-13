package com.example.service;

import com.example.mapper.RegistrationMapper;
import com.example.model.Registration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class RegistrationService {
    
    @Autowired
    private RegistrationMapper registrationMapper;
    
    @Autowired
    private ActivityService activityService;
    
    // 获取所有报名记录
    public List<Registration> getAllRegistrations() {
        return registrationMapper.findAll();
    }
    
    // 根据ID获取报名记录
    public Registration getRegistrationById(Long id) {
        return registrationMapper.findById(id);
    }
    
    // 根据用户ID获取报名记录
    public List<Registration> getRegistrationsByUserId(Long userId) {
        return registrationMapper.findByUserId(userId);
    }
    
    // 根据活动ID获取报名记录
    public List<Registration> getRegistrationsByActivityId(Long activityId) {
        return registrationMapper.findByActivityId(activityId);
    }
    
    // 根据状态获取报名记录
    public List<Registration> getRegistrationsByStatus(String status) {
        return registrationMapper.findByStatus(status);
    }
    
    // 用户报名活动
    @Transactional
    public boolean registerForActivity(Long userId, Long activityId, String notes) {
        // 检查用户是否已经报名
        if (isUserRegistered(userId, activityId)) {
            return false; // 已经报名
        }

        // 创建报名记录
        Registration registration = new Registration();
        registration.setUserId(userId);
        registration.setActivityId(activityId);
        registration.setStatus("registered");
        registration.setRegistrationTime(LocalDateTime.now());
        registration.setNotes(notes);

        // 插入报名记录，然后增加参与人数
        // activityService.incrementParticipants 会处理并发和名额检查
        activityService.incrementParticipants(activityId.intValue());
        return registrationMapper.insert(registration) > 0;
    }
    
    // 取消报名
    @Transactional
    public boolean cancelRegistration(Long userId, Long activityId) {
        Registration registration = registrationMapper.findByUserIdAndActivityId(userId, activityId);
        if (registration == null) {
            return false; // 未找到报名记录
        }

        // 更新状态为已取消
        registration.setStatus("cancelled");
        boolean success = registrationMapper.update(registration) > 0;

        // 如果更新成功，则减少参与人数
        if (success) {
            activityService.decrementParticipants(activityId.intValue());
        }

        return success;
    }
    
    // 删除报名记录
    public boolean deleteRegistration(Long id) {
        Registration registration = registrationMapper.findById(id);
        if (registration == null) {
            return false;
        }
        
        boolean success = registrationMapper.deleteById(id) > 0;
        
        // 如果删除成功且状态为已注册，减少活动参与人数
        if (success && "registered".equals(registration.getStatus())) {
            activityService.decrementParticipants(registration.getActivityId().intValue());
        }
        
        return success;
    }
    
    // 更新报名状态
    @Transactional
    public boolean updateRegistrationStatus(Long id, String status) {
        Registration registration = registrationMapper.findById(id);
        if (registration == null) {
            return false;
        }
        
        String oldStatus = registration.getStatus();
        registration.setStatus(status);
        boolean success = registrationMapper.update(registration) > 0;
        
        // 根据状态变化调整活动参与人数
        if (success) {
            Long activityId = registration.getActivityId();
            
            // 从已注册变为其他状态，减少人数
            if ("registered".equals(oldStatus) && !"registered".equals(status)) {
                activityService.decrementParticipants(activityId.intValue());
            }
            // 从其他状态变为已注册，增加人数
            else if (!"registered".equals(oldStatus) && "registered".equals(status)) {
                if (activityService.canRegister(activityId.intValue())) {
                    activityService.incrementParticipants(activityId.intValue());
                } else {
                    // 如果活动已满，恢复原状态
                    registration.setStatus(oldStatus);
                    registrationMapper.update(registration);
                    return false;
                }
            }
        }
        
        return success;
    }
    
    // 检查用户是否已报名某活动
    public boolean isUserRegistered(Long userId, Long activityId) {
        return registrationMapper.countByUserIdAndActivityId(userId, activityId) > 0;
    }
    
    // 获取活动的报名人数
    public int getRegisteredCountByActivity(Long activityId) {
        return registrationMapper.getRegisteredCountByActivityId(activityId);
    }
    
    // 获取用户的报名总数
    public int getRegistrationCountByUser(Long userId) {
        return registrationMapper.getRegistrationCountByUserId(userId);
    }
    
    // 分页获取报名记录
    public List<Registration> getRegistrationsWithPagination(int page, int size) {
        int offset = (page - 1) * size;
        return registrationMapper.findWithPagination(offset, size);
    }
    
    // 获取报名记录总数
    public int getTotalRegistrationCount() {
        return registrationMapper.getTotalCount();
    }
    
    // 获取用户参与的活动详情
    public List<Registration> getUserRegistrationsWithDetails(Long userId) {
        return registrationMapper.findUserRegistrationsWithActivityDetails(userId);
    }
    
    // 批量更新报名状态（管理员功能）
    @Transactional
    public boolean batchUpdateStatus(List<Long> registrationIds, String status) {
        boolean allSuccess = true;
        for (Long id : registrationIds) {
            if (!updateRegistrationStatus(id, status)) {
                allSuccess = false;
            }
        }
        return allSuccess;
    }
    
    // 同步所有活动的参与人数
    public void syncAllActivityParticipants() {
        // 这个方法可以定期调用来确保数据一致性
        List<Registration> allRegistrations = registrationMapper.findAll();
        
        // 按活动ID分组并同步
        allRegistrations.stream()
            .map(Registration::getActivityId)
            .distinct()
            .forEach(activityId -> activityService.syncParticipantCount(activityId.intValue()));
    }
}