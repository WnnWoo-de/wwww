package com.example.volunteerplatform.controller;

import java.util.HashMap;

import com.example.model.StoreItem;
import com.example.service.StoreItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.dto.ApiResponse;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/store")
@CrossOrigin(origins = "*")
public class StoreItemController {
    
    @Autowired
    private StoreItemService storeItemService;
    
    // 获取所有商品
    @GetMapping
    public ResponseEntity<ApiResponse<List<StoreItem>>> getAllStoreItems() {
        List<StoreItem> items = storeItemService.getAllStoreItems();
        return ResponseEntity.ok(ApiResponse.success("获取成功", items));
    }
    
    // 根据ID获取商品
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<StoreItem>> getStoreItemById(@PathVariable Long id) {
        StoreItem item = storeItemService.getStoreItemById(id);
        if (item != null) {
            return ResponseEntity.ok(ApiResponse.success("获取成功", item));
        }
        return ResponseEntity.status(404).body(ApiResponse.error("未找到商品"));
    }
    
    // 根据状态获取商品
    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<List<StoreItem>>> getStoreItemsByStatus(@PathVariable String status) {
        List<StoreItem> items = storeItemService.getStoreItemsByStatus(status);
        return ResponseEntity.ok(ApiResponse.success("获取成功", items));
    }
    
    // 根据分类获取商品
    @GetMapping("/category/{category}")
    public ResponseEntity<ApiResponse<List<StoreItem>>> getStoreItemsByCategory(@PathVariable String category) {
        List<StoreItem> items = storeItemService.getStoreItemsByCategory(category);
        return ResponseEntity.ok(ApiResponse.success("获取成功", items));
    }
    
    // 搜索商品
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<StoreItem>>> searchStoreItems(@RequestParam String name) {
        List<StoreItem> items = storeItemService.searchStoreItemsByName(name);
        return ResponseEntity.ok(ApiResponse.success("获取成功", items));
    }
    
    // 获取可购买的商品
    @GetMapping("/available")
    public ResponseEntity<ApiResponse<List<StoreItem>>> getAvailableStoreItems() {
        List<StoreItem> items = storeItemService.getAvailableStoreItems();
        return ResponseEntity.ok(ApiResponse.success("获取成功", items));
    }
    
    // 根据价格范围查询商品
    @GetMapping("/price-range")
    public ResponseEntity<ApiResponse<List<StoreItem>>> getStoreItemsByPriceRange(
            @RequestParam Double minPrice,
            @RequestParam Double maxPrice) {
        List<StoreItem> items = storeItemService.getStoreItemsByPriceRange(minPrice, maxPrice);
        return ResponseEntity.ok(ApiResponse.success("获取成功", items));
    }
    
    // 获取所有分类
    @GetMapping("/categories")
    public ResponseEntity<ApiResponse<List<String>>> getAllCategories() {
        List<String> categories = storeItemService.getAllCategories();
        return ResponseEntity.ok(ApiResponse.success("获取成功", categories));
    }
    
    // 创建新商品（管理员功能）
    @PostMapping
    public ResponseEntity<ApiResponse<StoreItem>> createStoreItem(@RequestBody StoreItem storeItem) {
        try {
            boolean success = storeItemService.createStoreItem(storeItem);
            if (success) {
                return ResponseEntity.ok(ApiResponse.success("商品创建成功", storeItem));
            } else {
                return ResponseEntity.badRequest().body(ApiResponse.error("商品创建失败"));
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("创建失败：" + e.getMessage()));
        }
    }
    
    // 更新商品信息（管理员功能）
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> updateStoreItem(
            @PathVariable Long id,
            @RequestBody StoreItem storeItem) {

        try {
            storeItem.setId(id);
            boolean success = storeItemService.updateStoreItem(storeItem);
            if (success) {
                return ResponseEntity.ok(ApiResponse.success("商品信息更新成功", null));
            } else {
                return ResponseEntity.badRequest().body(ApiResponse.error("商品不存在或更新失败"));
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("更新失败：" + e.getMessage()));
        }
    }
    
    // 删除商品（管理员功能）
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteStoreItem(@PathVariable Long id) {
        try {
            boolean success = storeItemService.deleteStoreItem(id);
            if (success) {
                return ResponseEntity.ok(ApiResponse.success("商品删除成功", null));
            } else {
                return ResponseEntity.badRequest().body(ApiResponse.error("商品不存在或删除失败"));
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("删除失败：" + e.getMessage()));
        }
    }
    
    // 更新库存（管理员功能）
    @PutMapping("/{id}/stock")
    public ResponseEntity<ApiResponse<StoreItem>> updateStock(
            @PathVariable Long id,
            @RequestBody Map<String, Integer> stockData) {

        try {
            Integer newStock = stockData.get("stock");
            boolean success = storeItemService.updateStock(id, newStock);

            if (success) {
                StoreItem item = storeItemService.getStoreItemById(id);
                return ResponseEntity.ok(ApiResponse.success("库存更新成功", item));
            } else {
                return ResponseEntity.badRequest().body(ApiResponse.error("商品不存在或库存更新失败"));
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("库存更新失败：" + e.getMessage()));
        }
    }
    
    // 购买商品（减少库存）
    @PostMapping("/{id}/purchase")
    public ResponseEntity<ApiResponse<StoreItem>> purchaseItem(
            @PathVariable Long id,
            @RequestBody Map<String, Integer> purchaseData) {

        try {
            Integer quantity = purchaseData.get("quantity");

            // 检查是否可购买
            if (!storeItemService.isAvailableForPurchase(id, quantity)) {
                return ResponseEntity.badRequest().body(ApiResponse.error("库存不足或商品不可购买"));
            }

            boolean success = storeItemService.purchaseItem(id, quantity);

            if (success) {
                StoreItem item = storeItemService.getStoreItemById(id);
                return ResponseEntity.ok(ApiResponse.success("购买成功", item));
            } else {
                return ResponseEntity.badRequest().body(ApiResponse.error("购买失败"));
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("购买失败：" + e.getMessage()));
        }
    }
    
    // 补货（增加库存）（管理员功能）
    @PostMapping("/{id}/restock")
    public ResponseEntity<ApiResponse<StoreItem>> restockItem(
            @PathVariable Long id, 
            @RequestBody Map<String, Integer> restockData) {
        
        try {
            Integer quantity = restockData.get("quantity");
            storeItemService.increaseStock(id, quantity);
            
            StoreItem item = storeItemService.getStoreItemById(id);
            return ResponseEntity.ok(ApiResponse.success("补货成功", item));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("补货失败：" + e.getMessage()));
        }
    }
    
    // 检查商品是否可购买
    @GetMapping("/{id}/check-availability")
    public ResponseEntity<ApiResponse<Map<String, Object>>> checkAvailability(
            @PathVariable Long id, 
            @RequestParam Integer quantity) {
        
        Map<String, Object> responseData = new HashMap<>();
        boolean available = storeItemService.isAvailableForPurchase(id, quantity);
        responseData.put("available", available);
        
        StoreItem item = storeItemService.getStoreItemById(id);
        if (item != null) {
            responseData.put("currentStock", item.getStock());
            responseData.put("status", item.getStatus());
        }
        
        return ResponseEntity.ok(ApiResponse.success("查询成功", responseData));
    }
    
    // 获取库存不足的商品（管理员功能）
    @GetMapping("/low-stock")
    public ResponseEntity<ApiResponse<List<StoreItem>>> getLowStockItems(
            @RequestParam(defaultValue = "10") Integer threshold) {
        List<StoreItem> items = storeItemService.getLowStockItems(threshold);
        return ResponseEntity.ok(ApiResponse.success("获取成功", items));
    }
    
    // 分页获取商品
    @GetMapping("/page")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getStoreItemsWithPagination(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {

        List<StoreItem> items = storeItemService.getStoreItemsWithPagination(page, size);
        int totalCount = storeItemService.getTotalStoreItemCount();
        int totalPages = (int) Math.ceil((double) totalCount / size);

        Map<String, Object> pageData = new HashMap<>();
        pageData.put("items", items);
        pageData.put("currentPage", page);
        pageData.put("totalPages", totalPages);
        pageData.put("totalCount", totalCount);
        pageData.put("pageSize", size);

        return ResponseEntity.ok(ApiResponse.success("获取分页数据成功", pageData));
    }
    
    // 获取商店统计信息（管理员功能）
    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<StoreItemService.StoreStats>> getStoreStats() {
        StoreItemService.StoreStats stats = storeItemService.getStoreStats();
        return ResponseEntity.ok(ApiResponse.success("获取成功", stats));
    }
    
    // 批量更新商品状态（管理员功能）
    @PutMapping("/batch/status")
    public ResponseEntity<ApiResponse<Void>> batchUpdateStatus(
            @RequestBody Map<String, Object> requestData) {
        
        try {
            @SuppressWarnings("unchecked")
            List<Long> itemIds = (List<Long>) requestData.get("itemIds");
            String status = (String) requestData.get("status");
            
            boolean success = storeItemService.batchUpdateStatus(itemIds, status);
            
            if (success) {
                return ResponseEntity.ok(ApiResponse.success("批量状态更新成功", null));
            } else {
                return ResponseEntity.badRequest().body(ApiResponse.error("部分或全部状态更新失败"));
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("批量更新失败：" + e.getMessage()));
        }
    }
}