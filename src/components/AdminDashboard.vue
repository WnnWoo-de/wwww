<template>
  <div class="admin-dashboard">
    <div class="admin-header">
      <h1>管理员控制台</h1>
      <div class="admin-actions">
        <span class="welcome-text">欢迎，管理员</span>
        <button @click="logout" class="logout-btn">退出登录</button>
      </div>
    </div>
    
    <div class="dashboard-content">
      <div class="stats-grid">
        <div class="stat-card">
          <div class="stat-icon">📅</div>
          <div class="stat-info">
            <h3>{{ stats.totalActivities }}</h3>
            <p>总活动数</p>
          </div>
        </div>
        
        <div class="stat-card">
          <div class="stat-icon">👥</div>
          <div class="stat-info">
            <h3>{{ stats.totalVolunteers }}</h3>
            <p>总志愿者数</p>
          </div>
        </div>
        
        <div class="stat-card">
          <div class="stat-icon">✅</div>
          <div class="stat-info">
            <h3>{{ stats.activeActivities }}</h3>
            <p>进行中活动</p>
          </div>
        </div>
        
        <div class="stat-card">
          <div class="stat-icon">⭐</div>
          <div class="stat-info">
            <h3>{{ stats.completedActivities }}</h3>
            <p>已完成活动</p>
          </div>
        </div>
      </div>
      
      <div class="management-grid">
        <div class="management-card" @click="navigateTo('/admin/activities')">
          <div class="card-icon">📋</div>
          <h3>活动管理</h3>
          <p>添加、编辑、删除志愿活动</p>
          <div class="card-arrow">→</div>
        </div>
        
        <div class="management-card" @click="navigateTo('/admin/volunteers')">
          <div class="card-icon">👤</div>
          <h3>志愿者管理</h3>
          <p>管理志愿者信息和状态</p>
          <div class="card-arrow">→</div>
        </div>
        
        <div class="management-card" @click="navigateTo('/admin/reports')">
          <div class="card-icon">📊</div>
          <h3>数据报告</h3>
          <p>查看统计数据和报告</p>
          <div class="card-arrow">→</div>
        </div>
        
        <div class="management-card" @click="navigateTo('/admin/settings')">
          <div class="card-icon">⚙️</div>
          <h3>系统设置</h3>
          <p>配置系统参数和权限</p>
          <div class="card-arrow">→</div>
        </div>
      </div>
      
      <div class="recent-activities">
        <h2>最近活动</h2>
        <div class="activity-list">
          <div v-for="activity in recentActivities" :key="activity.id" class="activity-item">
            <div class="activity-info">
              <h4>{{ activity.title }}</h4>
              <p>{{ activity.description }}</p>
              <span class="activity-date">{{ activity.date }}</span>
            </div>
            <div class="activity-status" :class="activity.status">
              {{ getStatusText(activity.status) }}
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

const stats = ref({
  totalActivities: 45,
  totalVolunteers: 128,
  activeActivities: 12,
  completedActivities: 33
})

const recentActivities = ref([
  {
    id: 1,
    title: '社区清洁活动',
    description: '组织志愿者清理社区环境',
    date: '2024-01-15',
    status: 'active'
  },
  {
    id: 2,
    title: '老人院慰问',
    description: '为老人院的老人们送去温暖',
    date: '2024-01-12',
    status: 'completed'
  },
  {
    id: 3,
    title: '图书馆整理',
    description: '帮助图书馆整理书籍',
    date: '2024-01-10',
    status: 'pending'
  }
])

const navigateTo = (path) => {
  router.push(path)
}

const logout = () => {
  router.push('/admin/login')
}

const getStatusText = (status) => {
  const statusMap = {
    active: '进行中',
    completed: '已完成',
    pending: '待开始'
  }
  return statusMap[status] || status
}

onMounted(() => {
  // 权限检查已移除
})
</script>

<style scoped>
.admin-dashboard {
  min-height: 100vh;
  background: #f5f7fa;
  padding: 20px;
}

.admin-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: white;
  padding: 20px 30px;
  border-radius: 12px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  margin-bottom: 30px;
}

.admin-header h1 {
  color: #333;
  margin: 0;
  font-size: 28px;
  font-weight: 600;
}

.admin-actions {
  display: flex;
  align-items: center;
  gap: 20px;
}

.welcome-text {
  color: #666;
  font-size: 16px;
}

.logout-btn {
  background: #ff6b6b;
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  transition: background 0.3s ease;
}

.logout-btn:hover {
  background: #ee5a52;
}

.dashboard-content {
  display: flex;
  flex-direction: column;
  gap: 30px;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
}

.stat-card {
  background: white;
  padding: 25px;
  border-radius: 12px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  display: flex;
  align-items: center;
  gap: 20px;
  transition: transform 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-2px);
}

.stat-icon {
  font-size: 40px;
  width: 60px;
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f8f9fa;
  border-radius: 12px;
}

.stat-info h3 {
  margin: 0 0 5px 0;
  font-size: 32px;
  font-weight: 700;
  color: #333;
}

.stat-info p {
  margin: 0;
  color: #666;
  font-size: 14px;
}

.management-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 20px;
}

.management-card {
  background: white;
  padding: 30px;
  border-radius: 12px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  cursor: pointer;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.management-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.15);
}

.management-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, #667eea, #764ba2);
}

.card-icon {
  font-size: 48px;
  margin-bottom: 15px;
}

.management-card h3 {
  margin: 0 0 10px 0;
  color: #333;
  font-size: 20px;
  font-weight: 600;
}

.management-card p {
  margin: 0;
  color: #666;
  font-size: 14px;
  line-height: 1.5;
}

.card-arrow {
  position: absolute;
  top: 20px;
  right: 20px;
  font-size: 20px;
  color: #ccc;
  transition: all 0.3s ease;
}

.management-card:hover .card-arrow {
  color: #667eea;
  transform: translateX(5px);
}

.recent-activities {
  background: white;
  padding: 30px;
  border-radius: 12px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.recent-activities h2 {
  margin: 0 0 20px 0;
  color: #333;
  font-size: 22px;
  font-weight: 600;
}

.activity-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.activity-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 8px;
  transition: background 0.3s ease;
}

.activity-item:hover {
  background: #e9ecef;
}

.activity-info h4 {
  margin: 0 0 5px 0;
  color: #333;
  font-size: 16px;
  font-weight: 600;
}

.activity-info p {
  margin: 0 0 5px 0;
  color: #666;
  font-size: 14px;
}

.activity-date {
  color: #999;
  font-size: 12px;
}

.activity-status {
  padding: 6px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 500;
}

.activity-status.active {
  background: #d4edda;
  color: #155724;
}

.activity-status.completed {
  background: #cce5ff;
  color: #004085;
}

.activity-status.pending {
  background: #fff3cd;
  color: #856404;
}

@media (max-width: 768px) {
  .admin-header {
    flex-direction: column;
    gap: 15px;
    text-align: center;
  }
  
  .stats-grid {
    grid-template-columns: 1fr;
  }
  
  .management-grid {
    grid-template-columns: 1fr;
  }
}
</style>