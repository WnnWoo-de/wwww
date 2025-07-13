package com.example.mapper;

import com.example.model.StoreItem;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface StoreItemMapper {
    
    // 查询所有商品
    @Select("SELECT * FROM store_items ORDER BY created_at DESC")
    List<StoreItem> findAll();
    
    // 根据ID查询商品
    @Select("SELECT * FROM store_items WHERE id = #{id}")
    StoreItem findById(Long id);
    
    // 根据状态查询商品
    @Select("SELECT * FROM store_items WHERE status = #{status} ORDER BY name ASC")
    List<StoreItem> findByStatus(String status);
    
    // 根据分类查询商品
    @Select("SELECT * FROM store_items WHERE category = #{category} ORDER BY name ASC")
    List<StoreItem> findByCategory(String category);
    
    // 根据名称模糊查询商品
    @Select("SELECT * FROM store_items WHERE name LIKE CONCAT('%', #{name}, '%') ORDER BY name ASC")
    List<StoreItem> findByNameContaining(String name);
    
    // 查询有库存的商品
    @Select("SELECT * FROM store_items WHERE stock > 0 AND status = 'available' ORDER BY name ASC")
    List<StoreItem> findAvailableItems();
    
    // 插入新商品
    @Insert("INSERT INTO store_items (name, description, price, stock, category, image_url, status, created_at, updated_at) " +
            "VALUES (#{name}, #{description}, #{price}, #{stock}, #{category}, #{imageUrl}, #{status}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(StoreItem storeItem);
    
    // 更新商品信息
    @Update("UPDATE store_items SET name = #{name}, description = #{description}, " +
            "price = #{price}, stock = #{stock}, category = #{category}, " +
            "image_url = #{imageUrl}, status = #{status}, version = version + 1, updated_at = NOW() " +
            "WHERE id = #{id} AND version = #{version}")
    int update(StoreItem storeItem);
    
    // 删除商品
    @Delete("DELETE FROM store_items WHERE id = #{id}")
    int deleteById(Long id);
    
    // 更新库存
    @Update("UPDATE store_items SET stock = #{stock}, updated_at = NOW() WHERE id = #{id}")
    int updateStock(@Param("id") Long id, @Param("stock") Integer stock);
    
    // 减少库存
    @Update("UPDATE store_items SET stock = stock - #{quantity}, version = version + 1, updated_at = NOW() " +
            "WHERE id = #{id} AND stock >= #{quantity}")
    int decreaseStock(@Param("id") Long id, @Param("quantity") Integer quantity);
    
    // 增加库存
    @Update("UPDATE store_items SET stock = stock + #{quantity}, version = version + 1, updated_at = NOW() WHERE id = #{id}")
    int increaseStock(@Param("id") Long id, @Param("quantity") Integer quantity);
    
    // 分页查询商品
    @Select("SELECT * FROM store_items ORDER BY created_at DESC LIMIT #{offset}, #{limit}")
    List<StoreItem> findWithPagination(@Param("offset") int offset, @Param("limit") int limit);
    
    // 获取商品总数
    @Select("SELECT COUNT(*) FROM store_items")
    int getTotalCount();
    
    // 根据状态获取商品数量
    @Select("SELECT COUNT(*) FROM store_items WHERE status = #{status}")
    int getCountByStatus(String status);
    
    // 获取库存不足的商品（库存小于等于指定数量）
    @Select("SELECT * FROM store_items WHERE stock <= #{threshold} ORDER BY stock ASC")
    List<StoreItem> findLowStockItems(Integer threshold);
    
    // 根据价格范围查询商品
    @Select("SELECT * FROM store_items WHERE price BETWEEN #{minPrice} AND #{maxPrice} " +
            "AND status = 'available' ORDER BY price ASC")
    List<StoreItem> findByPriceRange(@Param("minPrice") Double minPrice, @Param("maxPrice") Double maxPrice);
    
    // 获取所有分类
    @Select("SELECT DISTINCT category FROM store_items WHERE category IS NOT NULL ORDER BY category ASC")
    List<String> findAllCategories();
}