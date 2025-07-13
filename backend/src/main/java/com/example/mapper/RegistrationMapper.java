package com.example.mapper;

import com.example.model.Registration;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface RegistrationMapper {
    
    // 查询所有报名记录
    @Select("SELECT * FROM registrations ORDER BY registration_time DESC")
    List<Registration> findAll();
    
    // 根据ID查询报名记录
    @Select("SELECT * FROM registrations WHERE id = #{id}")
    Registration findById(Long id);
    
    // 根据用户ID查询报名记录
    @Select("SELECT * FROM registrations WHERE user_id = #{userId} ORDER BY registration_time DESC")
    List<Registration> findByUserId(Long userId);
    
    // 根据活动ID查询报名记录
    @Select("SELECT * FROM registrations WHERE activity_id = #{activityId} ORDER BY registration_time ASC")
    List<Registration> findByActivityId(Long activityId);
    
    // 根据用户ID和活动ID查询报名记录
    @Select("SELECT * FROM registrations WHERE user_id = #{userId} AND activity_id = #{activityId}")
    Registration findByUserIdAndActivityId(@Param("userId") Long userId, @Param("activityId") Long activityId);
    
    // 根据状态查询报名记录
    @Select("SELECT * FROM registrations WHERE status = #{status} ORDER BY registration_time DESC")
    List<Registration> findByStatus(String status);
    
    // 插入新报名记录
    @Insert("INSERT INTO registrations (user_id, activity_id, status, registration_time, notes) " +
            "VALUES (#{userId}, #{activityId}, #{status}, #{registrationTime}, #{notes})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Registration registration);
    
    // 更新报名记录
    @Update("UPDATE registrations SET status = #{status}, notes = #{notes} WHERE id = #{id}")
    int update(Registration registration);
    
    // 删除报名记录
    @Delete("DELETE FROM registrations WHERE id = #{id}")
    int deleteById(Long id);
    
    // 根据用户ID和活动ID删除报名记录
    @Delete("DELETE FROM registrations WHERE user_id = #{userId} AND activity_id = #{activityId}")
    int deleteByUserIdAndActivityId(@Param("userId") Long userId, @Param("activityId") Long activityId);
    
    // 检查用户是否已报名某活动
    @Select("SELECT COUNT(*) FROM registrations WHERE user_id = #{userId} AND activity_id = #{activityId}")
    int countByUserIdAndActivityId(@Param("userId") Long userId, @Param("activityId") Long activityId);
    
    // 获取活动的报名人数
    @Select("SELECT COUNT(*) FROM registrations WHERE activity_id = #{activityId} AND status = 'registered'")
    int getRegisteredCountByActivityId(Long activityId);
    
    // 获取用户的报名总数
    @Select("SELECT COUNT(*) FROM registrations WHERE user_id = #{userId}")
    int getRegistrationCountByUserId(Long userId);
    
    // 分页查询报名记录
    @Select("SELECT * FROM registrations ORDER BY registration_time DESC LIMIT #{offset}, #{limit}")
    List<Registration> findWithPagination(@Param("offset") int offset, @Param("limit") int limit);
    
    // 获取报名记录总数
    @Select("SELECT COUNT(*) FROM registrations")
    int getTotalCount();
    
    // 查询用户参与的活动详情（连接查询）
    @Select("SELECT r.*, a.title as activity_title, a.start_time, a.end_time, a.location " +
            "FROM registrations r JOIN activities a ON r.activity_id = a.id " +
            "WHERE r.user_id = #{userId} ORDER BY r.registration_time DESC")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "userId", column = "user_id"),
        @Result(property = "activityId", column = "activity_id"),
        @Result(property = "status", column = "status"),
        @Result(property = "registrationTime", column = "registration_time"),
        @Result(property = "notes", column = "notes")
    })
    List<Registration> findUserRegistrationsWithActivityDetails(Long userId);
}