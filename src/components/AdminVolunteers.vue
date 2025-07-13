<template>
  <div class="admin-volunteers">
    <div class="page-header">
      <h1>志愿者管理</h1>
      <button @click="showAddModal = true" class="add-btn">
        <span class="btn-icon">+</span>
        添加志愿者
      </button>
    </div>
    
    <div class="filters">
      <div class="search-box">
        <input
          v-model="searchQuery"
          type="text"
          placeholder="搜索志愿者姓名、邮箱或电话..."
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
    
    <div class="volunteers-table">
      <div class="table-header">
        <div class="header-cell">姓名</div>
        <div class="header-cell">邮箱</div>
        <div class="header-cell">电话</div>
        <div class="header-cell">注册日期</div>
        <div class="header-cell">状态</div>
        <div class="header-cell">参与活动数</div>
        <div class="header-cell">操作</div>
      </div>
      
      <div class="table-body">
        <div
          v-for="volunteer in filteredVolunteers"
          :key="volunteer.id"
          class="table-row"
        >
          <div class="cell">
            <div class="volunteer-info">
              <div class="avatar">{{ volunteer.name.charAt(0) }}</div>
              <span>{{ volunteer.name }}</span>
            </div>
          </div>
          <div class="cell">{{ volunteer.email }}</div>
          <div class="cell">{{ volunteer.phone }}</div>
          <div class="cell">{{ volunteer.registerDate }}</div>
          <div class="cell">
            <span :class="['status-badge', volunteer.status]">
              {{ getStatusText(volunteer.status) }}
            </span>
          </div>
          <div class="cell">{{ volunteer.activitiesCount }}</div>
          <div class="cell actions">
            <button @click="viewVolunteer(volunteer)" class="action-btn view">
              查看
            </button>
            <button @click="editVolunteer(volunteer)" class="action-btn edit">
              编辑
            </button>
            <button @click="deleteVolunteer(volunteer.id)" class="action-btn delete">
              删除
            </button>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 添加/编辑志愿者模态框 -->
    <div v-if="showAddModal || showEditModal" class="modal-overlay" @click="closeModal">
      <div class="modal" @click.stop>
        <div class="modal-header">
          <h2>{{ showAddModal ? '添加志愿者' : '编辑志愿者' }}</h2>
          <button @click="closeModal" class="close-btn">×</button>
        </div>
        
        <form @submit.prevent="saveVolunteer" class="modal-form">
          <div class="form-row">
            <div class="form-group">
              <label>姓名</label>
              <input
                v-model="currentVolunteer.name"
                type="text"
                required
                placeholder="请输入姓名"
              />
            </div>
            
            <div class="form-group">
              <label>邮箱</label>
              <input
                v-model="currentVolunteer.email"
                type="email"
                required
                placeholder="请输入邮箱"
              />
            </div>
          </div>
          
          <div class="form-row">
            <div class="form-group">
              <label>电话</label>
              <input
                v-model="currentVolunteer.phone"
                type="tel"
                required
                placeholder="请输入电话号码"
              />
            </div>
            
            <div class="form-group">
              <label>状态</label>
              <select v-model="currentVolunteer.status" required>
                <option value="active">活跃</option>
                <option value="inactive">不活跃</option>
                <option value="suspended">暂停</option>
              </select>
            </div>
          </div>
          
          <div class="form-group">
            <label>个人简介</label>
            <textarea
              v-model="currentVolunteer.bio"
              placeholder="请输入个人简介"
              rows="4"
            ></textarea>
          </div>
          
          <div class="form-row">
            <div class="form-group">
              <label>年龄</label>
              <input
                v-model.number="currentVolunteer.age"
                type="number"
                min="16"
                max="100"
                placeholder="请输入年龄"
              />
            </div>
            
            <div class="form-group">
              <label>技能标签</label>
              <input
                v-model="currentVolunteer.skills"
                type="text"
                placeholder="请输入技能标签，用逗号分隔"
              />
            </div>
          </div>
          
          <div class="modal-actions">
            <button type="button" @click="closeModal" class="cancel-btn">
              取消
            </button>
            <button type="submit" class="save-btn">
              {{ showAddModal ? '添加志愿者' : '保存修改' }}
            </button>
          </div>
        </form>
      </div>
    </div>
    
    <!-- 查看志愿者详情模态框 -->
    <div v-if="showViewModal" class="modal-overlay" @click="closeViewModal">
      <div class="modal view-modal" @click.stop>
        <div class="modal-header">
          <h2>志愿者详情</h2>
          <button @click="closeViewModal" class="close-btn">×</button>
        </div>
        
        <div class="volunteer-details">
          <div class="detail-section">
            <h3>基本信息</h3>
            <div class="detail-grid">
              <div class="detail-item">
                <label>姓名:</label>
                <span>{{ viewingVolunteer.name }}</span>
              </div>
              <div class="detail-item">
                <label>邮箱:</label>
                <span>{{ viewingVolunteer.email }}</span>
              </div>
              <div class="detail-item">
                <label>电话:</label>
                <span>{{ viewingVolunteer.phone }}</span>
              </div>
              <div class="detail-item">
                <label>年龄:</label>
                <span>{{ viewingVolunteer.age || '未填写' }}</span>
              </div>
              <div class="detail-item">
                <label>注册日期:</label>
                <span>{{ viewingVolunteer.registerDate }}</span>
              </div>
              <div class="detail-item">
                <label>状态:</label>
                <span :class="['status-badge', viewingVolunteer.status]">
                  {{ getStatusText(viewingVolunteer.status) }}
                </span>
              </div>
            </div>
          </div>
          
          <div class="detail-section" v-if="viewingVolunteer.bio">
            <h3>个人简介</h3>
            <p>{{ viewingVolunteer.bio }}</p>
          </div>
          
          <div class="detail-section" v-if="viewingVolunteer.skills">
            <h3>技能标签</h3>
            <div class="skills-tags">
              <span
                v-for="skill in viewingVolunteer.skills.split(',')"
                :key="skill"
                class="skill-tag"
              >
                {{ skill.trim() }}
              </span>
            </div>
          </div>
          
          <div class="detail-section">
            <h3>活动统计</h3>
            <div class="stats-grid">
              <div class="stat-item">
                <span class="stat-number">{{ viewingVolunteer.activitiesCount }}</span>
                <span class="stat-label">参与活动</span>
              </div>
              <div class="stat-item">
                <span class="stat-number">{{ viewingVolunteer.completedActivities || 0 }}</span>
                <span class="stat-label">完成活动</span>
              </div>
              <div class="stat-item">
                <span class="stat-number">{{ viewingVolunteer.volunteerHours || 0 }}</span>
                <span class="stat-label">志愿时长</span>
              </div>
            </div>
          </div>
        </div>
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
const showViewModal = ref(false)

const statusFilters = [
  { value: 'all', label: '全部' },
  { value: 'active', label: '活跃' },
  { value: 'inactive', label: '不活跃' },
  { value: 'suspended', label: '暂停' }
]

const volunteers = ref([
  {
    id: 1,
    name: '张三',
    email: 'zhangsan@example.com',
    phone: '13800138001',
    registerDate: '2024-01-01',
    status: 'active',
    activitiesCount: 5,
    age: 25,
    bio: '热爱公益事业，积极参与各种志愿活动',
    skills: '组织协调,沟通交流,活动策划',
    completedActivities: 4,
    volunteerHours: 32
  },
  {
    id: 2,
    name: '李四',
    email: 'lisi@example.com',
    phone: '13800138002',
    registerDate: '2024-01-05',
    status: 'active',
    activitiesCount: 3,
    age: 30,
    bio: '教师职业，喜欢帮助他人',
    skills: '教学辅导,心理疏导',
    completedActivities: 3,
    volunteerHours: 24
  },
  {
    id: 3,
    name: '王五',
    email: 'wangwu@example.com',
    phone: '13800138003',
    registerDate: '2024-01-10',
    status: 'inactive',
    activitiesCount: 1,
    age: 22,
    bio: '大学生，希望通过志愿服务锻炼自己',
    skills: '计算机技术,英语翻译',
    completedActivities: 1,
    volunteerHours: 8
  }
])

const currentVolunteer = ref({
  name: '',
  email: '',
  phone: '',
  status: 'active',
  bio: '',
  age: '',
  skills: ''
})

const viewingVolunteer = ref({})

const filteredVolunteers = computed(() => {
  let filtered = volunteers.value
  
  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase()
    filtered = filtered.filter(volunteer =>
      volunteer.name.toLowerCase().includes(query) ||
      volunteer.email.toLowerCase().includes(query) ||
      volunteer.phone.includes(query)
    )
  }
  
  if (selectedStatus.value !== 'all') {
    filtered = filtered.filter(volunteer => volunteer.status === selectedStatus.value)
  }
  
  return filtered
})

const getStatusText = (status) => {
  const statusMap = {
    active: '活跃',
    inactive: '不活跃',
    suspended: '暂停'
  }
  return statusMap[status] || status
}

const viewVolunteer = (volunteer) => {
  viewingVolunteer.value = volunteer
  showViewModal.value = true
}

const editVolunteer = (volunteer) => {
  currentVolunteer.value = { ...volunteer }
  showEditModal.value = true
}

const deleteVolunteer = (id) => {
  if (confirm('确定要删除这个志愿者吗？')) {
    volunteers.value = volunteers.value.filter(volunteer => volunteer.id !== id)
  }
}

const saveVolunteer = () => {
  if (showAddModal.value) {
    // 添加新志愿者
    const newVolunteer = {
      ...currentVolunteer.value,
      id: Date.now(),
      registerDate: new Date().toISOString().split('T')[0],
      activitiesCount: 0,
      completedActivities: 0,
      volunteerHours: 0
    }
    volunteers.value.push(newVolunteer)
  } else {
    // 编辑现有志愿者
    const index = volunteers.value.findIndex(v => v.id === currentVolunteer.value.id)
    if (index !== -1) {
      volunteers.value[index] = { ...currentVolunteer.value }
    }
  }
  
  closeModal()
}

const closeModal = () => {
  showAddModal.value = false
  showEditModal.value = false
  currentVolunteer.value = {
    name: '',
    email: '',
    phone: '',
    status: 'active',
    bio: '',
    age: '',
    skills: ''
  }
}

const closeViewModal = () => {
  showViewModal.value = false
  viewingVolunteer.value = {}
}

onMounted(() => {
  // 检查管理员权限
  const adminToken = localStorage.getItem('adminToken')
  const userRole = localStorage.getItem('userRole')
  
  if (!adminToken || userRole !== 'admin') {
    router.push('/admin/login')
  }
})
</script>

<style scoped>
.admin-volunteers {
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

.volunteers-table {
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.table-header {
  display: grid;
  grid-template-columns: 2fr 2.5fr 1.5fr 1.5fr 1fr 1fr 1.5fr;
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
  grid-template-columns: 2fr 2.5fr 1.5fr 1.5fr 1fr 1fr 1.5fr;
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

.volunteer-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
  font-size: 16px;
}

.status-badge {
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 500;
}

.status-badge.active {
  background: #d4edda;
  color: #155724;
}

.status-badge.inactive {
  background: #f8d7da;
  color: #721c24;
}

.status-badge.suspended {
  background: #fff3cd;
  color: #856404;
}

.actions {
  gap: 8px;
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

.action-btn.view {
  background: #e8f5e8;
  color: #2e7d32;
}

.action-btn.view:hover {
  background: #2e7d32;
  color: white;
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

.view-modal {
  max-width: 700px;
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

.volunteer-details {
  padding: 30px;
}

.detail-section {
  margin-bottom: 30px;
}

.detail-section h3 {
  margin: 0 0 15px 0;
  color: #333;
  font-size: 18px;
  font-weight: 600;
  border-bottom: 2px solid #f1f3f4;
  padding-bottom: 8px;
}

.detail-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 15px;
}

.detail-item {
  display: flex;
  align-items: center;
  gap: 10px;
}

.detail-item label {
  font-weight: 500;
  color: #666;
  min-width: 80px;
}

.skills-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.skill-tag {
  background: #e3f2fd;
  color: #1976d2;
  padding: 4px 12px;
  border-radius: 16px;
  font-size: 12px;
  font-weight: 500;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
}

.stat-item {
  text-align: center;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 8px;
}

.stat-number {
  display: block;
  font-size: 24px;
  font-weight: 700;
  color: #333;
  margin-bottom: 5px;
}

.stat-label {
  font-size: 14px;
  color: #666;
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
  
  .detail-grid {
    grid-template-columns: 1fr;
  }
  
  .stats-grid {
    grid-template-columns: 1fr;
  }
}
</style>