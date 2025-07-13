package com.example.volunteerplatform.controller;

import com.example.dto.ApiResponse;
import com.example.model.Registration;
import com.example.service.RegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/registrations")
@CrossOrigin(origins = "*")
public class RegistrationController {
    
    @Autowired
    private RegistrationService registrationService;
    
    // 获取所有报名记录（管理员功能）
    @GetMapping
    public ResponseEntity<ApiResponse<List<Registration>>> getAllRegistrations() {
        List<Registration> registrations = registrationService.getAllRegistrations();
        return ResponseEntity.ok(ApiResponse.success("获取成功", registrations));
    }
    
    // 根据ID获取报名记录
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Registration>> getRegistrationById(@PathVariable Long id) {
        Registration registration = registrationService.getRegistrationById(id);
        if (registration != null) {
            return ResponseEntity.ok(ApiResponse.success("获取成功", registration));
        }
        return ResponseEntity.status(404).body(ApiResponse.error("未找到报名记录"));
    }
    
    // 根据用户ID获取报名记录
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<Registration>>> getRegistrationsByUserId(@PathVariable Long userId) {
        List<Registration> registrations = registrationService.getRegistrationsByUserId(userId);
        return ResponseEntity.ok(ApiResponse.success("获取成功", registrations));
    }
    
    // 根据活动ID获取报名记录
    @GetMapping("/activity/{activityId}")
    public ResponseEntity<ApiResponse<List<Registration>>> getRegistrationsByActivityId(@PathVariable Long activityId) {
        List<Registration> registrations = registrationService.getRegistrationsByActivityId(activityId);
        return ResponseEntity.ok(ApiResponse.success("获取成功", registrations));
    }
    
    // 根据状态获取报名记录
    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<List<Registration>>> getRegistrationsByStatus(@PathVariable String status) {
        List<Registration> registrations = registrationService.getRegistrationsByStatus(status);
        return ResponseEntity.ok(ApiResponse.success("获取成功", registrations));
    }
    
    // 用户报名活动
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Void>> registerForActivity(@RequestBody Map<String, Object> requestData) {
        try {
            Long userId = Long.valueOf(requestData.get("userId").toString());
            Long activityId = Long.valueOf(requestData.get("activityId").toString());
            String notes = (String) requestData.get("notes");

            boolean success = registrationService.registerForActivity(userId, activityId, notes);

            if (success) {
                return ResponseEntity.ok(ApiResponse.success("报名成功", null));
            } else {
                return ResponseEntity.badRequest().body(ApiResponse.error("报名失败：可能已经报名或活动已满"));
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("报名失败：" + e.getMessage()));
        }
    }
    
    // 取消报名
    @PostMapping("/cancel")
    public ResponseEntity<ApiResponse<Void>> cancelRegistration(@RequestBody Map<String, Object> requestData) {
        try {
            Long userId = Long.valueOf(requestData.get("userId").toString());
            Long activityId = Long.valueOf(requestData.get("activityId").toString());

            boolean success = registrationService.cancelRegistration(userId, activityId);

            if (success) {
                return ResponseEntity.ok(ApiResponse.success("取消报名成功", null));
            } else {
                return ResponseEntity.badRequest().body(ApiResponse.error("取消报名失败：未找到报名记录"));
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("取消报名失败：" + e.getMessage()));
        }
    }
    
    // 删除报名记录（管理员功能）
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteRegistration(@PathVariable Long id) {
        try {
            boolean success = registrationService.deleteRegistration(id);
            if (success) {
                return ResponseEntity.ok(ApiResponse.success("报名记录删除成功", null));
            } else {
                return ResponseEntity.badRequest().body(ApiResponse.error("报名记录不存在或删除失败"));
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("删除失败：" + e.getMessage()));
        }
    }
    
    // 更新报名状态（管理员功能）
    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<Void>> updateRegistrationStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> statusData) {

        try {
            String status = statusData.get("status");
            boolean success = registrationService.updateRegistrationStatus(id, status);

            if (success) {
                return ResponseEntity.ok(ApiResponse.success("报名状态更新成功", null));
            } else {
                return ResponseEntity.badRequest().body(ApiResponse.error("报名记录不存在或状态更新失败"));
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("状态更新失败：" + e.getMessage()));
        }
    }
    
    // 检查用户是否已报名某活动
    @GetMapping("/check")
    public ResponseEntity<ApiResponse<Boolean>> checkRegistration(
            @RequestParam Long userId,
            @RequestParam Long activityId) {

        boolean isRegistered = registrationService.isUserRegistered(userId, activityId);
        return ResponseEntity.ok(ApiResponse.success("查询成功", isRegistered));
    }
    
    // 获取活动的报名人数
    @GetMapping("/activity/{activityId}/count")
    public ResponseEntity<ApiResponse<Integer>> getRegistrationCountByActivity(@PathVariable Long activityId) {
        int count = registrationService.getRegisteredCountByActivity(activityId);
        return ResponseEntity.ok(ApiResponse.success("查询成功", count));
    }
    
    // 获取用户的报名总数
    @GetMapping("/user/{userId}/count")
    public ResponseEntity<ApiResponse<Integer>> getRegistrationCountByUser(@PathVariable Long userId) {
        int count = registrationService.getRegistrationCountByUser(userId);
        return ResponseEntity.ok(ApiResponse.success("查询成功", count));
    }
    
    // 分页获取报名记录
    @GetMapping("/page")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getRegistrationsWithPagination(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {

        List<Registration> registrations = registrationService.getRegistrationsWithPagination(page, size);
        int totalCount = registrationService.getTotalRegistrationCount();
        int totalPages = (int) Math.ceil((double) totalCount / size);

        Map<String, Object> pageData = new HashMap<>();
        pageData.put("registrations", registrations);
        pageData.put("currentPage", page);
        pageData.put("totalPages", totalPages);
        pageData.put("totalCount", totalCount);
        pageData.put("pageSize", size);

        return ResponseEntity.ok(ApiResponse.success("获取分页数据成功", pageData));
    }
    
    // 获取用户参与的活动详情
    @GetMapping("/user/{userId}/details")
    public ResponseEntity<ApiResponse<List<Registration>>> getUserRegistrationsWithDetails(@PathVariable Long userId) {
        List<Registration> registrations = registrationService.getUserRegistrationsWithDetails(userId);
        return ResponseEntity.ok(ApiResponse.success("获取成功", registrations));
    }
    
    // 批量更新报名状态（管理员功能）
    @PutMapping("/batch/status")
    public ResponseEntity<ApiResponse<Void>> batchUpdateStatus(
            @RequestBody Map<String, Object> requestData) {

        try {
            @SuppressWarnings("unchecked")
            List<Long> registrationIds = (List<Long>) requestData.get("registrationIds");
            String status = (String) requestData.get("status");

            boolean success = registrationService.batchUpdateStatus(registrationIds, status);

            if (success) {
                return ResponseEntity.ok(ApiResponse.success("批量状态更新成功", null));
            } else {
                return ResponseEntity.badRequest().body(ApiResponse.error("部分或全部状态更新失败"));
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("批量更新失败：" + e.getMessage()));
        }
    }
    
    // 同步所有活动的参与人数（管理员功能）
    @PostMapping("/sync-all")
    public ResponseEntity<ApiResponse<Void>> syncAllActivityParticipants() {
        try {
            registrationService.syncAllActivityParticipants();
            return ResponseEntity.ok(ApiResponse.success("所有活动参与人数同步成功", null));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("同步失败：" + e.getMessage()));
        }
    }
    
    // 获取报名统计信息（管理员功能）
    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getRegistrationStats() {
        Map<String, Object> stats = new HashMap<>();

        int totalRegistrations = registrationService.getTotalRegistrationCount();
        int registeredCount = registrationService.getRegistrationsByStatus("registered").size();
        int attendedCount = registrationService.getRegistrationsByStatus("attended").size();
        int cancelledCount = registrationService.getRegistrationsByStatus("cancelled").size();

        stats.put("totalRegistrations", totalRegistrations);
        stats.put("registeredCount", registeredCount);
        stats.put("attendedCount", attendedCount);
        stats.put("cancelledCount", cancelledCount);

        return ResponseEntity.ok(ApiResponse.success("获取统计信息成功", stats));
    }
}