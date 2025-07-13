package com.example.volunteerplatform.controller;

import com.example.dto.ApiResponse;
import com.example.dto.AuthenticationRequest;
import com.example.dto.AuthenticationResponse;
import com.example.model.User;
import com.example.volunteerplatform.exception.ResourceNotFoundException;
import com.example.service.UserService;
import com.example.util.JwtUtil;

import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {
    
    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;
    
    // 获取所有用户（管理员功能）
    @GetMapping
    public ResponseEntity<ApiResponse<List<User>>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(ApiResponse.success(users));
    }
    
    // 根据ID获取用户
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<User>> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        if (user == null) {
            String currentPrincipalName = null;
            throw new ResourceNotFoundException("User not found with email: " + currentPrincipalName);
        }
        return ResponseEntity.ok(ApiResponse.success(user));
    }
    
    // 根据角色获取用户
    @GetMapping("/role/{role}")
    public ResponseEntity<ApiResponse<List<User>>> getUsersByRole(@PathVariable String role) {
        List<User> users = userService.getUsersByRole(role);
        return ResponseEntity.ok(ApiResponse.success(users));
    }
    
    // 用户注册
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<User>> registerUser(@RequestBody User user) {
        try {
            boolean success = userService.registerUser(user);
            if (success) {
                return ResponseEntity.ok(ApiResponse.success("用户注册成功", user));
            } else {
                return ResponseEntity.badRequest().body(ApiResponse.error("用户名或邮箱已存在"));
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("注册失败：" + e.getMessage()));
        }
    }
    
    // 用户登录
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthenticationResponse>> createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword())
            );
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(401).body(ApiResponse.error("用户名或密码错误"));
        }

        final UserDetails userDetails = userService.loadUserByUsername(authenticationRequest.getUsername());
        final String jwt = jwtUtil.generateToken(userDetails);

        return ResponseEntity.ok(ApiResponse.success(new AuthenticationResponse(jwt)));
    }
    
    // 更新用户信息
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> updateUser(@PathVariable Long id, @RequestBody User user) {
        try {
            user.setId(id);
            boolean success = userService.updateUser(user);
            if (success) {
                return ResponseEntity.ok(ApiResponse.success("用户信息更新成功", null));
            } else {
                return ResponseEntity.badRequest().body(ApiResponse.error("用户不存在或更新失败"));
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("更新失败：" + e.getMessage()));
        }
    }
    
    // 删除用户（管理员功能）
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
        try {
            boolean success = userService.deleteUser(id);
            if (success) {
                return ResponseEntity.ok(ApiResponse.success("用户删除成功", null));
            } else {
                return ResponseEntity.badRequest().body(ApiResponse.error("用户不存在或删除失败"));
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("删除失败：" + e.getMessage()));
        }
    }
    
    // 检查邮箱是否存在
    @GetMapping("/check-email/{email}")
    public ResponseEntity<ApiResponse<Boolean>> checkEmail(@PathVariable String email) {
        boolean exists = userService.isEmailExists(email);
        return ResponseEntity.ok(ApiResponse.success(exists));
    }
    
    // 检查用户名是否存在
    @GetMapping("/check-username/{username}")
    public ResponseEntity<ApiResponse<Boolean>> checkUsername(@PathVariable String username) {
        boolean exists = userService.isUsernameExists(username);
        return ResponseEntity.ok(ApiResponse.success(exists));
    }
    
    // 分页获取用户
    @GetMapping("/page")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getUsersWithPagination(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {

        List<User> users = userService.getUsersWithPagination(page, size);
        int totalCount = userService.getTotalUserCount();
        int totalPages = (int) Math.ceil((double) totalCount / size);

        Map<String, Object> data = new HashMap<>();
        data.put("users", users);
        data.put("currentPage", page);
        data.put("totalPages", totalPages);
        data.put("totalCount", totalCount);
        data.put("pageSize", size);

        return ResponseEntity.ok(ApiResponse.success(data));
    }
    
    // 修改密码
    @PutMapping("/{id}/password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @PathVariable Long id,
            @RequestBody Map<String, String> passwordData) {

        try {
            String newPassword = passwordData.get("newPassword");
            boolean success = userService.changePassword(id, newPassword);

            if (success) {
                return ResponseEntity.ok(ApiResponse.success("密码修改成功", null));
            } else {
                return ResponseEntity.badRequest().body(ApiResponse.error("用户不存在或密码修改失败"));
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("密码修改失败：" + e.getMessage()));
        }
    }
    
    // 修改用户角色（管理员功能）
    @PutMapping("/{id}/role")
    public ResponseEntity<Map<String, Object>> changeUserRole(
            @PathVariable Long id, 
            @RequestBody Map<String, String> roleData) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            String newRole = roleData.get("role");
            boolean success = userService.changeUserRole(id, newRole);
            
            if (success) {
                response.put("success", true);
                response.put("message", "用户角色修改成功");
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "用户不存在或角色修改失败");
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "角色修改失败：" + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    // 获取用户统计信息（管理员功能）
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getUserStats() {
        Map<String, Object> stats = new HashMap<>();
        
        int totalUsers = userService.getTotalUserCount();
        int adminUsers = userService.getUsersByRole("admin").size();
        int regularUsers = userService.getUsersByRole("user").size();
        
        stats.put("totalUsers", totalUsers);
        stats.put("adminUsers", adminUsers);
        stats.put("regularUsers", regularUsers);
        
        return ResponseEntity.ok(stats);
    }

    // 获取当前登录用户的个人资料
    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<User>> getUserProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();
        User user = userService.findByEmail(currentPrincipalName);
        if (user != null) {
            return ResponseEntity.ok(ApiResponse.success(user));
        }
        return ResponseEntity.status(404).body(ApiResponse.error("用户未找到"));
    }
}