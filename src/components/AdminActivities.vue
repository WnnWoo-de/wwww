<template>
  <div class="admin-activities">
    <div class="page-header">
      <h1>活动管理</h1>
      <button @click="showAddModal = true" class="add-btn">
        <span class="btn-icon">+</span>
        添加新活动
      </button>
    </div>
    
    <div class="filters">
      <div class="search-box">
        <input
          v-model="searchQuery"
          type="text"
          placeholder="搜索活动名称或描述..."
          class="search-input"
        />
      </div>
      <div class="filter-buttons">
        <button
          v-for="status in statusFilters"
          :key="status.value"
          @click="selectedStatus = status.value"
          :class="['filter-btn', { active: selectedStatus === status.value }]"
        >
          {{ status.label }}
        </button>
      </div>
    </div>
    
    <div class="activities-table">
      <div class="table-header">
        <div class="header-cell">活动名称</div>
        <div class="header-cell">描述</div>
        <div class="header-cell">日期</div>
        <div class="header-cell">状态</div>
        <div class="header-cell">参与人数</div>
        <div class="header-cell">操作</div>
      </div>
      
      <div class="table-body">
        <div
          v-for="activity in filteredActivities"
          :key="activity.id"
          class="table-row"
        >
          <div class="cell">{{ activity.title }}</div>
          <div class="cell">{{ activity.description }}</div>
          <div class="cell">{{ activity.date }}</div>
          <div class="cell">
            <span :class="['status-badge', activity.status]">
              {{ getStatusText(activity.status) }}
            </span>
          </div>
          <div class="cell">{{ activity.participants }}</div>
          <div class="cell actions">
            <button @click="editActivity(activity)" class="action-btn edit">
              编辑
            </button>
            <button @click="deleteActivity(activity.id)" class="action-btn delete">
              删除
            </button>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 添加/编辑活动模态框 -->
    <div v-if="showAddModal || showEditModal" class="modal-overlay" @click="closeModal">
      <div class="modal" @click.stop>
        <div class="modal-header">
          <h2>{{ showAddModal ? '添加新活动' : '编辑活动' }}</h2>
          <button @click="closeModal" class="close-btn">×</button>
        </div>
        
        <form @submit.prevent="saveActivity" class="modal-form">
          <div class="form-group">
            <label>活动名称</label>
            <input
              v-model="currentActivity.title"
              type="text"
              required
              placeholder="请输入活动名称"
            />
          </div>
          
          <div class="form-group">
            <label>活动描述</label>
            <textarea
              v-model="currentActivity.description"
              required
              placeholder="请输入活动描述"
              rows="4"
            ></textarea>
          </div>
          
          <div class="form-row">
            <div class="form-group">
              <label>活动日期</label>
              <input
                v-model="currentActivity.date"
                type="date"
                required
              />
            </div>
            
            <div class="form-group">
              <label>活动状态</label>
              <select v-model="currentActivity.status" required>
                <option value="pending">待开始</option>
                <option value="active">进行中</option>
                <option value="completed">已完成</option>
              </select>
            </div>
          </div>
          
          <div class="form-row">
            <div class="form-group">
              <label>最大参与人数</label>
              <input
                v-model.number="currentActivity.maxParticipants"
                type="number"
                min="1"
                required
                placeholder="请输入最大参与人数"
              />
            </div>
            
            <div class="form-group">
              <label>活动地点</label>
              <input
                v-model="currentActivity.location"
                type="text"
                required
                placeholder="请输入活动地点"
              />
            </div>
          </div>
          
          <div class="modal-actions">
            <button type="button" @click="closeModal" class="cancel-btn">
              取消
            </button>
            <button type="submit" class="save-btn">
              {{ showAddModal ? '添加活动' : '保存修改' }}
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

const searchQuery = ref('')
const selectedStatus = ref('all')
const showAddModal = ref(false)
const showEditModal = ref(false)

const statusFilters = [
  { value: 'all', label: '全部' },
  { value: 'pending', label: '待开始' },
  { value: 'active', label: '进行中' },
  { value: 'completed', label: '已完成' }
]

import api from '@/api/index.js';

const activities = ref([]);

const currentActivity = ref({
  title: '',
  description: '',
  date: '',
  status: 'pending',
  participants: 0,
  maxParticipants: '',
  location: ''
})

const filteredActivities = computed(() => {
  let filtered = activities.value
  
  if (searchQuery.value) {
    filtered = filtered.filter(activity =>
      activity.title.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
      activity.description.toLowerCase().includes(searchQuery.value.toLowerCase())
    )
  }
  
  if (selectedStatus.value !== 'all') {
    filtered = filtered.filter(activity => activity.status === selectedStatus.value)
  }
  
  return filtered
})

const getStatusText = (status) => {
  const statusMap = {
    pending: '待开始',
    active: '进行中',
    completed: '已完成'
  }
  return statusMap[status] || status
}

const editActivity = (activity) => {
  currentActivity.value = { ...activity }
  showEditModal.value = true
}

const deleteActivity = (id) => {
  if (confirm('确定要删除这个活动吗？')) {
    activities.value = activities.value.filter(activity => activity.id !== id)
  }
}

const saveActivity = async () => {
  try {
    if (showAddModal.value) {
      // 添加新活动
      const response = await api.post('/activities', currentActivity.value);
      if (response.data.success) {
        activities.value.push(response.data.data);
      } else {
        alert(response.data.message);
      }
    } else {
      // 更新活动
      const response = await api.put(`/activities/${currentActivity.value.id}`, currentActivity.value);
      if (response.data.success) {
        const index = activities.value.findIndex(
          (a) => a.id === currentActivity.value.id
        );
        if (index !== -1) {
          activities.value[index] = { ...currentActivity.value };
        }
      } else {
        alert(response.data.message);
      }
    }
    closeModal();
  } catch (error) {
    console.error('保存活动失败:', error);
    alert('操作失败，请稍后重试');
  }
};

const closeModal = () => {
  showAddModal.value = false
  showEditModal.value = false
  currentActivity.value = {
    title: '',
    description: '',
    date: '',
    status: 'pending',
    participants: 0,
    maxParticipants: '',
    location: ''
  }
}

onMounted(async () => {
  // 检查管理员权限
  const adminToken = localStorage.getItem('adminToken')
  const userRole = localStorage.getItem('userRole')
  
  if (!adminToken || userRole !== 'admin') {
    router.push('/admin/login')
    return
  }

  try {
    const response = await api.get('/activities');
    if (response.data.success) {
      activities.value = response.data.data;
    } else {
      alert(response.data.message);
    }
  } catch (error) {
    console.error('获取活动列表失败:', error);
  }
});
</script>

<style scoped>
.admin-activities {
  padding: 20px;
  background: #f5f7fa;
  min-height: 100vh;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
}

.page-header h1 {
  color: #333;
  margin: 0;
  font-size: 28px;
  font-weight: 600;
}

.add-btn {
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: white;
  border: none;
  padding: 12px 24px;
  border-radius: 8px;
  cursor: pointer;
  font-size: 16px;
  font-weight: 500;
  display: flex;
  align-items: center;
  gap: 8px;
  transition: all 0.3s ease;
}

.add-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(102, 126, 234, 0.3);
}

.btn-icon {
  font-size: 20px;
  font-weight: bold;
}

.filters {
  background: white;
  padding: 20px;
  border-radius: 12px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  margin-bottom: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 20px;
}

.search-box {
  flex: 1;
  max-width: 400px;
}

.search-input {
  width: 100%;
  padding: 12px 16px;
  border: 2px solid #e1e5e9;
  border-radius: 8px;
  font-size: 16px;
  transition: border-color 0.3s ease;
}

.search-input:focus {
  outline: none;
  border-color: #667eea;
}

.filter-buttons {
  display: flex;
  gap: 10px;
}

.filter-btn {
  padding: 8px 16px;
  border: 2px solid #e1e5e9;
  background: white;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s ease;
}

.filter-btn:hover {
  border-color: #667eea;
  color: #667eea;
}

.filter-btn.active {
  background: #667eea;
  border-color: #667eea;
  color: white;
}

.activities-table {
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.table-header {
  display: grid;
  grid-template-columns: 2fr 3fr 1.5fr 1fr 1fr 1.5fr;
  background: #f8f9fa;
  padding: 20px;
  font-weight: 600;
  color: #333;
  border-bottom: 1px solid #e1e5e9;
}

.table-body {
  max-height: 600px;
  overflow-y: auto;
}

.table-row {
  display: grid;
  grid-template-columns: 2fr 3fr 1.5fr 1fr 1fr 1.5fr;
  padding: 20px;
  border-bottom: 1px solid #f1f3f4;
  transition: background 0.3s ease;
}

.table-row:hover {
  background: #f8f9fa;
}

.cell {
  display: flex;
  align-items: center;
  color: #333;
  font-size: 14px;
}

.status-badge {
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 500;
}

.status-badge.pending {
  background: #fff3cd;
  color: #856404;
}

.status-badge.active {
  background: #d4edda;
  color: #155724;
}

.status-badge.completed {
  background: #cce5ff;
  color: #004085;
}

.actions {
  gap: 10px;
}

.action-btn {
  padding: 6px 12px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 12px;
  font-weight: 500;
  transition: all 0.3s ease;
}

.action-btn.edit {
  background: #e3f2fd;
  color: #1976d2;
}

.action-btn.edit:hover {
  background: #1976d2;
  color: white;
}

.action-btn.delete {
  background: #ffebee;
  color: #d32f2f;
}

.action-btn.delete:hover {
  background: #d32f2f;
  color: white;
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal {
  background: white;
  border-radius: 12px;
  width: 90%;
  max-width: 600px;
  max-height: 90vh;
  overflow-y: auto;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 30px;
  border-bottom: 1px solid #e1e5e9;
}

.modal-header h2 {
  margin: 0;
  color: #333;
  font-size: 24px;
  font-weight: 600;
}

.close-btn {
  background: none;
  border: none;
  font-size: 24px;
  cursor: pointer;
  color: #666;
  padding: 0;
  width: 30px;
  height: 30px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.close-btn:hover {
  color: #333;
}

.modal-form {
  padding: 30px;
}

.form-group {
  margin-bottom: 20px;
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  color: #333;
  font-weight: 500;
  font-size: 14px;
}

.form-group input,
.form-group textarea,
.form-group select {
  width: 100%;
  padding: 12px 16px;
  border: 2px solid #e1e5e9;
  border-radius: 8px;
  font-size: 16px;
  transition: border-color 0.3s ease;
  box-sizing: border-box;
}

.form-group input:focus,
.form-group textarea:focus,
.form-group select:focus {
  outline: none;
  border-color: #667eea;
}

.modal-actions {
  display: flex;
  justify-content: flex-end;
  gap: 15px;
  margin-top: 30px;
}

.cancel-btn {
  padding: 12px 24px;
  border: 2px solid #e1e5e9;
  background: white;
  color: #666;
  border-radius: 8px;
  cursor: pointer;
  font-size: 16px;
  transition: all 0.3s ease;
}

.cancel-btn:hover {
  border-color: #ccc;
  color: #333;
}

.save-btn {
  padding: 12px 24px;
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-size: 16px;
  font-weight: 500;
  transition: all 0.3s ease;
}

.save-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(102, 126, 234, 0.3);
}

@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
    gap: 15px;
    align-items: stretch;
  }
  
  .filters {
    flex-direction: column;
    gap: 15px;
  }
  
  .table-header,
  .table-row {
    grid-template-columns: 1fr;
    gap: 10px;
  }
  
  .header-cell,
  .cell {
    padding: 5px 0;
  }
  
  .form-row {
    grid-template-columns: 1fr;
  }
}
</style>