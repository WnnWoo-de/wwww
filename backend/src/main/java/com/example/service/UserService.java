package com.example.service;

import com.example.mapper.UserMapper;
import com.example.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserService implements UserDetailsService {
    
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    // 获取所有用户
    public List<User> getAllUsers() {
        return userMapper.findAll();
    }
    
    // 根据ID获取用户
    public User getUserById(Long id) {
        return userMapper.findById(id);
    }
    
    // 根据邮箱获取用户
    public User getUserByEmail(String email) {
        return userMapper.findByEmail(email);
    }
    
    // 根据用户名获取用户
    public User getUserByUsername(String username) {
        return userMapper.findByUsername(username);
    }

    public User findByEmail(String email) {
        return userMapper.findByEmail(email);
    }
    
    // 根据角色获取用户列表
    public List<User> getUsersByRole(String role) {
        return userMapper.findByRole(role);
    }
    
    // 用户注册
    public boolean registerUser(User user) {
        // 检查邮箱是否已存在
        if (userMapper.countByEmail(user.getEmail()) > 0) {
            return false; // 邮箱已存在
        }
        
        // 检查用户名是否已存在
        if (userMapper.countByUsername(user.getUsername()) > 0) {
            return false; // 用户名已存在
        }
        
        // 设置默认角色
        if (user.getRole() == null || user.getRole().isEmpty()) {
            user.setRole("user");
        }

        // 加密密码
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // 设置创建和更新时间
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        
        // 插入用户数据
        return userMapper.insert(user) > 0;
    }
    

    
    // 更新用户信息
    public boolean updateUser(User user) {
        return userMapper.update(user) > 0;
    }
    
    // 删除用户
    public boolean deleteUser(Long id) {
        return userMapper.deleteById(id) > 0;
    }
    
    // 检查邮箱是否存在
    public boolean isEmailExists(String email) {
        return userMapper.countByEmail(email) > 0;
    }
    
    // 检查用户名是否存在
    public boolean isUsernameExists(String username) {
        return userMapper.countByUsername(username) > 0;
    }
    
    // 分页获取用户
    public List<User> getUsersWithPagination(int page, int size) {
        int offset = (page - 1) * size;
        return userMapper.findWithPagination(offset, size);
    }
    
    // 获取用户总数
    public int getTotalUserCount() {
        return userMapper.getTotalCount();
    }
    
    // 修改用户密码
    public boolean changePassword(Long userId, String newPassword) {
        User user = userMapper.findById(userId);
        if (user != null) {
            user.setPassword(passwordEncoder.encode(newPassword));
            return userMapper.update(user) > 0;
        }
        return false;
    }
    
    // 修改用户角色（仅管理员可操作）
    public boolean changeUserRole(Long userId, String newRole) {
        User user = userMapper.findById(userId);
        if (user != null) {
            user.setRole(newRole);
            return userMapper.update(user) > 0;
        }
        return false;
    }
    
    // 验证用户权限
    public boolean hasPermission(Long userId, String requiredRole) {
        User user = userMapper.findById(userId);
        return user != null && requiredRole.equals(user.getRole());
    }

    @Override
    public org.springframework.security.core.userdetails.UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userMapper.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with email: " + email);
        }
        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), new ArrayList<>());
    }
}