package com.example.mapper;

import com.example.model.Activity;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface ActivityMapper {
    
    // 查询所有活动
    @Select("SELECT * FROM activities ORDER BY created_at DESC")
    List<Activity> findAll();
    
    // 根据ID查询活动
    @Select("SELECT * FROM activities WHERE id = #{id}")
    Activity findById(Integer id);
    
    // 根据状态查询活动
    @Select("SELECT * FROM activities WHERE status = #{status} ORDER BY start_time ASC")
    List<Activity> findByStatus(String status);
    
    // 根据分类查询活动
    @Select("SELECT * FROM activities WHERE category = #{category} ORDER BY start_time ASC")
    List<Activity> findByCategory(String category);
    
    // 根据标题模糊查询活动
    @Select("SELECT * FROM activities WHERE title LIKE CONCAT('%', #{title}, '%') ORDER BY created_at DESC")
    List<Activity> findByTitleContaining(String title);
    
    // 插入新活动
    @Insert("INSERT INTO activities (title, description, location, start_time, end_time, " +
            "max_participants, current_participants, status, category, created_at, updated_at) " +
            "VALUES (#{title}, #{description}, #{location}, #{startTime}, #{endTime}, " +
            "#{maxParticipants}, #{currentParticipants}, #{status}, #{category}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Activity activity);
    
    // 更新活动信息
    @Update("UPDATE activities SET title = #{title}, description = #{description}, " +
            "location = #{location}, start_time = #{startTime}, end_time = #{endTime}, " +
            "max_participants = #{maxParticipants}, current_participants = #{currentParticipants}, " +
            "status = #{status}, category = #{category}, version = version + 1, updated_at = NOW() " +
            "WHERE id = #{id} AND version = #{version}")
    int update(Activity activity);
    
    // 删除活动
    @Delete("DELETE FROM activities WHERE id = #{id}")
    int deleteById(Integer id);
    
    // 更新当前参与人数
    @Update("UPDATE activities SET current_participants = #{currentParticipants}, " +
            "updated_at = NOW() WHERE id = #{id}")
    int updateCurrentParticipants(@Param("id") Integer id, @Param("currentParticipants") Integer currentParticipants);
    
    // 增加参与人数
    @Update("UPDATE activities SET current_participants = current_participants + 1, version = version + 1, " +
            "updated_at = NOW() WHERE id = #{id} AND current_participants < max_participants")
    int incrementParticipants(Integer id);
    
    // 减少参与人数
    @Update("UPDATE activities SET current_participants = current_participants - 1, version = version + 1, " +
            "updated_at = NOW() WHERE id = #{id} AND current_participants > 0")
    int decrementParticipants(Integer id);
    
    // 分页查询活动
    @Select("SELECT * FROM activities ORDER BY created_at DESC LIMIT #{offset}, #{limit}")
    List<Activity> findWithPagination(@Param("offset") int offset, @Param("limit") int limit);
    
    // 获取活动总数
    @Select("SELECT COUNT(*) FROM activities")
    int getTotalCount();
    
    // 根据状态获取活动数量
    @Select("SELECT COUNT(*) FROM activities WHERE status = #{status}")
    int getCountByStatus(String status);
    
    // 查询即将开始的活动
    @Select("SELECT * FROM activities WHERE status = 'active' AND start_time > NOW() " +
            "ORDER BY start_time ASC LIMIT #{limit}")
    List<Activity> findUpcomingActivities(int limit);
}