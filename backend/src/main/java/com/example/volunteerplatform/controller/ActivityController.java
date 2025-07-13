package com.example.volunteerplatform.controller;

import com.example.service.ActivityDTO;
import com.example.dto.ApiResponse;
import com.example.model.Activity;
import com.example.service.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Page;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/activities")
@CrossOrigin(origins = "*")
public class ActivityController {
    
    @Autowired
    private ActivityService activityService;
    
    // 获取所有活动
    @GetMapping
    public ResponseEntity<ApiResponse<List<ActivityDTO>>> getAllActivities() {
        List<ActivityDTO> activities = activityService.getAllActivities();
        return ResponseEntity.ok(ApiResponse.success(activities));
    }
    
    // 根据ID获取活动
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ActivityDTO>> getActivityById(@PathVariable Integer id) {
        ActivityDTO activity = activityService.getActivityById(id);
        if (activity != null) {
            return ResponseEntity.ok(ApiResponse.success(activity));
        }
        return ResponseEntity.status(404).body(ApiResponse.error("活动未找到"));
    }
    
    // 根据状态获取活动
    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<List<Activity>>> getActivitiesByStatus(@PathVariable String status) {
        List<Activity> activities = activityService.getActivitiesByStatus(status);
        return ResponseEntity.ok(ApiResponse.success(activities));
    }
    
    // 根据分类获取活动
    @GetMapping("/category/{category}")
    public ResponseEntity<ApiResponse<List<Activity>>> getActivitiesByCategory(@PathVariable String category) {
        List<Activity> activities = activityService.getActivitiesByCategory(category);
        return ResponseEntity.ok(ApiResponse.success(activities));
    }
    
    // 搜索活动
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<Activity>>> searchActivities(@RequestParam String title) {
        List<Activity> activities = activityService.searchActivitiesByTitle(title);
        return ResponseEntity.ok(ApiResponse.success(activities));
    }
    
    // 获取即将开始的活动
    @GetMapping("/upcoming")
    public ResponseEntity<ApiResponse<List<Activity>>> getUpcomingActivities(
            @RequestParam(defaultValue = "5") int limit) {
        List<Activity> activities = activityService.getUpcomingActivities(limit);
        return ResponseEntity.ok(ApiResponse.success(activities));
    }
    
    // 创建新活动（管理员功能）
    @PostMapping
    public ResponseEntity<ApiResponse<Activity>> createActivity(@RequestBody ActivityDTO activityDTO) {
        try {
            Activity activity = convertToEntity(activityDTO);
            boolean success = activityService.createActivity(activity);
            if (success) {
                return ResponseEntity.ok(ApiResponse.success("活动创建成功", activity));
            } else {
                return ResponseEntity.badRequest().body(ApiResponse.error("活动创建失败"));
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("创建失败：" + e.getMessage()));
        }
    }
    
    // 更新活动信息（管理员功能）
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> updateActivity(
            @PathVariable Integer id,
            @RequestBody ActivityDTO activityDTO) {

        try {
            Activity activity = convertToEntity(activityDTO);
            activity.setId(id.longValue());
            boolean success = activityService.updateActivity(activity);
            if (success) {
                return ResponseEntity.ok(ApiResponse.success("活动信息更新成功", null));
            } else {
                return ResponseEntity.badRequest().body(ApiResponse.error("活动不存在或更新失败"));
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("更新失败：" + e.getMessage()));
        }
    }
    
    // 删除活动（管理员功能）
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteActivity(@PathVariable Integer id) {
        try {
            boolean success = activityService.deleteActivity(id);
            if (success) {
                return ResponseEntity.ok(ApiResponse.success("活动删除成功", null));
            } else {
                return ResponseEntity.badRequest().body(ApiResponse.error("活动不存在或删除失败"));
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("删除失败：" + e.getMessage()));
        }
    }
    
    // 更新活动状态（管理员功能）
    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<Void>> updateActivityStatus(
            @PathVariable Integer id,
            @RequestBody Map<String, String> statusData) {

        try {
            String status = statusData.get("status");
            boolean success = activityService.updateActivityStatus(id, status);
            if (success) {
                return ResponseEntity.ok(ApiResponse.success("活动状态更新成功", null));
            } else {
                return ResponseEntity.badRequest().body(ApiResponse.error("活动不存在或状态更新失败"));
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("状态更新失败：" + e.getMessage()));
        }
    }
    
    // 检查活动是否可以报名
    @GetMapping("/{id}/can-register")
    public ResponseEntity<ApiResponse<Boolean>> canRegister(@PathVariable Integer id) {
        boolean canRegister = activityService.canRegister(id);
        return ResponseEntity.ok(ApiResponse.success("查询成功", canRegister));
    }
    
    // 分页获取活动
    @GetMapping("/paged")
    public ResponseEntity<ApiResponse<Page<ActivityDTO>>> getActivitiesByPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        try {
            Page<Activity> activityPage = activityService.getActivitiesByPage(page, size);
            Page<ActivityDTO> activityDtoPage = activityPage.map(this::convertToDto);
            return ResponseEntity.ok(ApiResponse.success("获取分页数据成功", activityDtoPage));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("获取分页数据失败：" + e.getMessage()));
        }
    }
    
    // 获取活动统计信息（管理员功能）
    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<ActivityService.ActivityStats>> getActivityStats() {
        ActivityService.ActivityStats stats = activityService.getActivityStats();
        return ResponseEntity.ok(ApiResponse.success("获取成功", stats));
    }
    
    // 同步活动参与人数（管理员功能）
    @PostMapping("/{id}/sync-participants")
    public ResponseEntity<ApiResponse<ActivityDTO>> syncParticipantCount(@PathVariable Integer id) {
        try {
            boolean success = activityService.syncParticipantCount(id);
            if (success) {
                // 返回更新后的活动信息
                ActivityDTO activity = activityService.getActivityById(id);
                return ResponseEntity.ok(ApiResponse.success("参与人数同步成功", activity));
            } else {
                return ResponseEntity.badRequest().body(ApiResponse.error("活动不存在或同步失败"));
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("同步失败：" + e.getMessage()));
        }
    }
    
    // 批量更新活动状态（管理员功能）
    @PutMapping("/batch/status")
    public ResponseEntity<ApiResponse<Map<String, Object>>> batchUpdateStatus(
            @RequestBody Map<String, Object> requestData) {

        try {
            @SuppressWarnings("unchecked")
            List<Integer> activityIds = (List<Integer>) requestData.get("activityIds");
            String status = (String) requestData.get("status");

            int successCount = 0;
            for (Integer id : activityIds) {
                if (activityService.updateActivityStatus(id, status)) {
                    successCount++;
                }
            }

            Map<String, Object> result = new HashMap<>();
            result.put("successCount", successCount);
            result.put("totalCount", activityIds.size());

            return ResponseEntity.ok(ApiResponse.success(String.format("成功更新 %d/%d 个活动状态", successCount, activityIds.size()), result));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("批量更新失败：" + e.getMessage()));
        }
    }
    
    private Activity convertToEntity(ActivityDTO activityDTO) {
        Activity activity = new Activity();
        if (activityDTO.getId() != null) {
            activity.setId(activityDTO.getId().longValue());
        }
        activity.setTitle(activityDTO.getTitle());
        activity.setDescription(activityDTO.getDescription());
        activity.setStartTime(activityDTO.getStartTime());
        activity.setEndTime(activityDTO.getEndTime());
        activity.setLocation(activityDTO.getLocation());
        // 使用requiredVolunteers或maxParticipants，优先使用maxParticipants
        if (activityDTO.getMaxParticipants() != null) {
            activity.setMaxParticipants(activityDTO.getMaxParticipants());
        } else if (activityDTO.getRequiredVolunteers() != null) {
            activity.setMaxParticipants(activityDTO.getRequiredVolunteers());
        }
        activity.setCurrentParticipants(activityDTO.getCurrentParticipants());
        activity.setStatus(activityDTO.getStatus());
        activity.setCategory(activityDTO.getCategory());
        return activity;
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