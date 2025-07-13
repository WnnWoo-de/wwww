package com.example.service;

import com.example.mapper.StoreItemMapper;
import com.example.model.StoreItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class StoreItemService {
    
    @Autowired
    private StoreItemMapper storeItemMapper;
    
    // 获取所有商品
    public List<StoreItem> getAllStoreItems() {
        return storeItemMapper.findAll();
    }
    
    // 根据ID获取商品
    public StoreItem getStoreItemById(Long id) {
        return storeItemMapper.findById(id);
    }
    
    // 根据状态获取商品
    public List<StoreItem> getStoreItemsByStatus(String status) {
        return storeItemMapper.findByStatus(status);
    }
    
    // 根据分类获取商品
    public List<StoreItem> getStoreItemsByCategory(String category) {
        return storeItemMapper.findByCategory(category);
    }
    
    // 根据名称搜索商品
    public List<StoreItem> searchStoreItemsByName(String name) {
        return storeItemMapper.findByNameContaining(name);
    }
    
    // 获取可购买的商品
    public List<StoreItem> getAvailableStoreItems() {
        return storeItemMapper.findAvailableItems();
    }
    
    // 创建新商品
    public boolean createStoreItem(StoreItem storeItem) {
        // 设置默认状态
        if (storeItem.getStatus() == null || storeItem.getStatus().isEmpty()) {
            storeItem.setStatus("available");
        }
        
        // 根据库存自动设置状态
        if (storeItem.getStock() != null && storeItem.getStock() <= 0) {
            storeItem.setStatus("out_of_stock");
        }
        
        return storeItemMapper.insert(storeItem) > 0;
    }
    
    // 更新商品信息
    public boolean updateStoreItem(StoreItem storeItem) {
        // 根据库存自动调整状态
        if (storeItem.getStock() != null) {
            if (storeItem.getStock() <= 0 && "available".equals(storeItem.getStatus())) {
                storeItem.setStatus("out_of_stock");
            } else if (storeItem.getStock() > 0 && "out_of_stock".equals(storeItem.getStatus())) {
                storeItem.setStatus("available");
            }
        }
        
        return storeItemMapper.update(storeItem) > 0;
    }
    
    // 删除商品
    public boolean deleteStoreItem(Long id) {
        return storeItemMapper.deleteById(id) > 0;
    }
    
    // 更新库存
    @Transactional
    public boolean updateStock(Long id, Integer newStock) {
        StoreItem item = storeItemMapper.findById(id);
        if (item == null) {
            return false;
        }
        
        boolean success = storeItemMapper.updateStock(id, newStock) > 0;
        
        // 根据新库存更新状态
        if (success) {
            String newStatus;
            if (newStock <= 0) {
                newStatus = "out_of_stock";
            } else if ("out_of_stock".equals(item.getStatus())) {
                newStatus = "available";
            } else {
                newStatus = item.getStatus();
            }
            
            if (!newStatus.equals(item.getStatus())) {
                item.setStatus(newStatus);
                item.setStock(newStock);
                storeItemMapper.update(item);
            }
        }
        
        return success;
    }
    
    // 减少库存（购买时调用）
    @Transactional
    public void decreaseStock(Long id, Integer quantity) {
        int updatedRows = storeItemMapper.decreaseStock(id, quantity);
        if (updatedRows == 0) {
            throw new RuntimeException("库存扣减失败，商品可能已售罄或不存在。");
        }
    }
    
    // 增加库存（补货时调用）
    @Transactional
    public void increaseStock(Long id, Integer quantity) {
        int updatedRows = storeItemMapper.increaseStock(id, quantity);
        if (updatedRows == 0) {
            throw new RuntimeException("库存增加失败，可能存在并发问题。");
        }
    }
    
    // 检查商品是否可购买
    public boolean isAvailableForPurchase(Long id, Integer quantity) {
        StoreItem item = storeItemMapper.findById(id);
        return item != null && 
               "available".equals(item.getStatus()) && 
               item.getStock() >= quantity;
    }
    
    // 分页获取商品
    public List<StoreItem> getStoreItemsWithPagination(int page, int size) {
        int offset = (page - 1) * size;
        return storeItemMapper.findWithPagination(offset, size);
    }
    
    // 获取商品总数
    public int getTotalStoreItemCount() {
        return storeItemMapper.getTotalCount();
    }
    
    // 根据状态获取商品数量
    public int getStoreItemCountByStatus(String status) {
        return storeItemMapper.getCountByStatus(status);
    }
    
    // 获取库存不足的商品
    public List<StoreItem> getLowStockItems(Integer threshold) {
        if (threshold == null) {
            threshold = 10; // 默认阈值
        }
        return storeItemMapper.findLowStockItems(threshold);
    }
    
    // 根据价格范围查询商品
    public List<StoreItem> getStoreItemsByPriceRange(Double minPrice, Double maxPrice) {
        return storeItemMapper.findByPriceRange(minPrice, maxPrice);
    }
    
    // 获取所有分类
    public List<String> getAllCategories() {
        return storeItemMapper.findAllCategories();
    }
    
    // 获取商品统计信息
    public StoreStats getStoreStats() {
        StoreStats stats = new StoreStats();
        stats.setTotalItems(storeItemMapper.getTotalCount());
        stats.setAvailableItems(storeItemMapper.getCountByStatus("available"));
        stats.setOutOfStockItems(storeItemMapper.getCountByStatus("out_of_stock"));
        stats.setDiscontinuedItems(storeItemMapper.getCountByStatus("discontinued"));
        stats.setLowStockItems(storeItemMapper.findLowStockItems(10).size());
        return stats;
    }
    
    // 批量更新商品状态
    @Transactional
    public boolean batchUpdateStatus(List<Long> itemIds, String status) {
        boolean allSuccess = true;
        for (Long id : itemIds) {
            StoreItem item = storeItemMapper.findById(id);
            if (item != null) {
                item.setStatus(status);
                if (!(storeItemMapper.update(item) > 0)) {
                    allSuccess = false;
                }
            } else {
                allSuccess = false;
            }
        }
        return allSuccess;
    }
    
    // 内部类：商店统计信息
    public static class StoreStats {
        private int totalItems;
        private int availableItems;
        private int outOfStockItems;
        private int discontinuedItems;
        private int lowStockItems;
        
        // Getter和Setter方法
        public int getTotalItems() {
            return totalItems;
        }
        
        public void setTotalItems(int totalItems) {
            this.totalItems = totalItems;
        }
        
        public int getAvailableItems() {
            return availableItems;
        }
        
        public void setAvailableItems(int availableItems) {
            this.availableItems = availableItems;
        }
        
        public int getOutOfStockItems() {
            return outOfStockItems;
        }
        
        public void setOutOfStockItems(int outOfStockItems) {
            this.outOfStockItems = outOfStockItems;
        }
        
        public int getDiscontinuedItems() {
            return discontinuedItems;
        }
        
        public void setDiscontinuedItems(int discontinuedItems) {
            this.discontinuedItems = discontinuedItems;
        }
        
        public int getLowStockItems() {
            return lowStockItems;
        }
        
        public void setLowStockItems(int lowStockItems) {
            this.lowStockItems = lowStockItems;
        }
    }

    public boolean purchaseItem(Long id, Integer quantity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'purchaseItem'");
    }
}