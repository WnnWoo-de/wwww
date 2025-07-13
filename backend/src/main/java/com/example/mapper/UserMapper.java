package com.example.mapper;

import com.example.model.User;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface UserMapper {
    
    // 查询所有用户
    @Select("SELECT * FROM users")
    List<User> findAll();
    
    // 根据ID查询用户
    @Select("SELECT * FROM users WHERE id = #{id}")
    User findById(Long id);
    
    // 根据邮箱查询用户
    @Select("SELECT * FROM users WHERE email = #{email}")
    User findByEmail(String email);
    
    // 根据用户名查询用户
    @Select("SELECT * FROM users WHERE username = #{username}")
    User findByUsername(String username);
    
    // 根据角色查询用户
    @Select("SELECT * FROM users WHERE role = #{role}")
    List<User> findByRole(String role);
    
    // 插入新用户
    @Insert("INSERT INTO users (username, email, password, role, created_at, updated_at) " +
            "VALUES (#{username}, #{email}, #{password}, #{role}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(User user);
    
    // 更新用户信息
    @Update("UPDATE users SET username = #{username}, email = #{email}, " +
            "password = #{password}, role = #{role}, updated_at = NOW() WHERE id = #{id}")
    int update(User user);
    
    // 删除用户
    @Delete("DELETE FROM users WHERE id = #{id}")
    int deleteById(Long id);
    
    // 检查邮箱是否存在
    @Select("SELECT COUNT(*) FROM users WHERE email = #{email}")
    int countByEmail(String email);
    
    // 检查用户名是否存在
    @Select("SELECT COUNT(*) FROM users WHERE username = #{username}")
    int countByUsername(String username);
    

    
    // 分页查询用户
    @Select("SELECT * FROM users LIMIT #{offset}, #{limit}")
    List<User> findWithPagination(@Param("offset") int offset, @Param("limit") int limit);
    
    // 获取用户总数
    @Select("SELECT COUNT(*) FROM users")
    int getTotalCount();
}