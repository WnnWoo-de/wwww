<template>
  <div class="activities-page">
    <!-- Page Header -->
    <div class="page-header">
      <h1>活动广场</h1>
      <p>发现有意义的志愿活动，用行动传递爱心</p>
    </div>

    <!-- Search and Filter -->
    <div class="search-section">
      <div class="search-container">
        <div class="search-bar">
          <input 
            type="text" 
            v-model="searchQuery" 
            placeholder="搜索活动名称、地点或关键词..."
            class="search-input"
          >
          <button class="search-btn">🔍</button>
        </div>
        
        <div class="filters">
          <select v-model="selectedCategory" class="filter-select">
            <option value="">所有分类</option>
            <option value="环保">环保公益</option>
            <option value="教育">教育支教</option>
            <option value="养老">关爱老人</option>
            <option value="儿童">关爱儿童</option>
            <option value="社区">社区服务</option>
          </select>
          
          <select v-model="selectedStatus" class="filter-select">
            <option value="">所有状态</option>
            <option value="报名中">报名中</option>
            <option value="进行中">进行中</option>
            <option value="已结束">已结束</option>
          </select>
          
          <select v-model="selectedLocation" class="filter-select">
            <option value="">所有地区</option>
            <option value="市中心">市中心</option>
            <option value="郊区">郊区</option>
            <option value="在线">在线活动</option>
          </select>
        </div>
      </div>
    </div>

    <!-- Activities Grid -->
    <div class="activities-container">
      <div class="activities-grid">
        <div 
          v-for="activity in filteredActivities" 
          :key="activity.id" 
          class="activity-card"
          @click="selectActivity(activity)"
        >
          <div class="activity-image">
            <img :src="activity.image" :alt="activity.title">
            <div class="activity-status" :class="getStatusClass(activity.status)">
              {{ activity.status }}
            </div>
            <div class="activity-category">{{ activity.category }}</div>
          </div>
          
          <div class="activity-content">
            <h3 class="activity-title">{{ activity.title }}</h3>
            <p class="activity-description">{{ activity.description }}</p>
            
            <div class="activity-meta">
              <div class="meta-item">
                <span class="meta-icon">📅</span>
                <span>{{ formatDate(activity.date) }}</span>
              </div>
              <div class="meta-item">
                <span class="meta-icon">📍</span>
                <span>{{ activity.location }}</span>
              </div>
              <div class="meta-item">
                <span class="meta-icon">⏰</span>
                <span>{{ activity.duration }}</span>
              </div>
            </div>
            
            <div class="activity-footer">
              <div class="participants-info">
                <span class="participants-count">👥 {{ activity.participants }}/{{ activity.maxParticipants }}</span>
                <div class="progress-bar">
                  <div 
                    class="progress-fill" 
                    :style="{ width: (activity.participants / activity.maxParticipants * 100) + '%' }"
                  ></div>
                </div>
              </div>
              
              <div class="activity-points">
                <span class="points">+{{ activity.points }} 积分</span>
              </div>
            </div>
            
            <button 
              class="btn btn-primary btn-register"
              :disabled="activity.status !== '报名中'"
              @click.stop="registerActivity(activity)"
            >
              {{ getButtonText(activity.status) }}
            </button>
          </div>
        </div>
      </div>
      
      <!-- Empty State -->
      <div v-if="filteredActivities.length === 0" class="empty-state">
        <div class="empty-icon">🔍</div>
        <h3>没有找到相关活动</h3>
        <p>试试调整搜索条件或浏览其他分类</p>
      </div>
    </div>

    <!-- Activity Detail Modal -->
    <div v-if="selectedActivityDetail" class="modal-overlay" @click="closeModal">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h2>{{ selectedActivityDetail.title }}</h2>
          <button class="close-btn" @click="closeModal">×</button>
        </div>
        
        <div class="modal-body">
          <img :src="selectedActivityDetail.image" :alt="selectedActivityDetail.title" class="modal-image">
          
          <div class="activity-details">
            <p class="detail-description">{{ selectedActivityDetail.fullDescription }}</p>
            
            <div class="detail-info">
              <div class="info-row">
                <strong>活动时间：</strong>{{ formatDate(selectedActivityDetail.date) }}
              </div>
              <div class="info-row">
                <strong>活动地点：</strong>{{ selectedActivityDetail.location }}
              </div>
              <div class="info-row">
                <strong>活动时长：</strong>{{ selectedActivityDetail.duration }}
              </div>
              <div class="info-row">
                <strong>获得积分：</strong>{{ selectedActivityDetail.points }} 分
              </div>
              <div class="info-row">
                <strong>报名人数：</strong>{{ selectedActivityDetail.participants }}/{{ selectedActivityDetail.maxParticipants }}
              </div>
            </div>
            
            <div class="organizer-info">
              <h4>主办方信息</h4>
              <p>{{ selectedActivityDetail.organizer }}</p>
            </div>
          </div>
        </div>
        
        <div class="modal-footer">
          <button class="btn btn-secondary" @click="closeModal">关闭</button>
          <button 
            class="btn btn-primary"
            :disabled="selectedActivityDetail.status !== '报名中'"
            @click="registerActivity(selectedActivityDetail)"
          >
            {{ getButtonText(selectedActivityDetail.status) }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, defineEmits } from 'vue'

const emit = defineEmits(['show-login'])

// 搜索和筛选
const searchQuery = ref('')
const selectedCategory = ref('')
const selectedStatus = ref('')
const selectedLocation = ref('')
const selectedActivityDetail = ref(null)

// 模拟活动数据
const activities = ref([
  {
    id: 1,
    title: '关爱空巢老人志愿服务',
    description: '陪伴社区独居老人，为他们带去温暖和关怀，帮助解决日常生活中的困难',
    fullDescription: '本次活动旨在关爱社区内的空巢老人，通过志愿者的陪伴和帮助，让老人们感受到社会的温暖。活动内容包括：陪老人聊天、帮助打扫卫生、协助购买生活用品、教授使用智能手机等。',
    image: 'https://plus.unsplash.com/premium_photo-1663100162202-c1e6fa87bc5a?q=80&w=687&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D',
    category: '养老',
    date: '2024-01-15',
    location: '阳光社区',
    duration: '3小时',
    participants: 12,
    maxParticipants: 20,
    points: 30,
    status: '报名中',
    organizer: '阳光社区志愿者协会'
  },
  {
    id: 2,
    title: '环保清洁行动',
    description: '保护环境从我做起，清理公园垃圾，宣传环保理念',
    fullDescription: '地球是我们共同的家园，保护环境需要每个人的参与。本次环保清洁行动将在滨江公园进行，志愿者们将清理公园内的垃圾，向游客宣传环保知识，共同维护美丽的生态环境。',
    image: 'https://plus.unsplash.com/premium_photo-1664372899236-9d0654e2507f?q=80&w=695&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D',
    category: '环保',
    date: '2024-01-18',
    location: '滨江公园',
    duration: '4小时',
    participants: 25,
    maxParticipants: 30,
    points: 40,
    status: '报名中',
    organizer: '绿色地球环保组织'
  },
  {
    id: 3,
    title: '儿童阅读推广活动',
    description: '为山区儿童带去知识的力量和阅读的快乐',
    fullDescription: '阅读是孩子们认识世界的窗口。本次活动将前往希望小学，为孩子们带去精彩的故事和有趣的图书，通过互动阅读的方式，激发孩子们对知识的渴望和对阅读的热爱。',
    image: 'https://plus.unsplash.com/premium_photo-1664105110798-0530b3e0d153?q=80&w=687&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D',
    category: '教育',
    date: '2024-01-20',
    location: '希望小学',
    duration: '5小时',
    participants: 8,
    maxParticipants: 15,
    points: 50,
    status: '报名中',
    organizer: '知识传播志愿团'
  },
  {
    id: 4,
    title: '社区义诊服务',
    description: '为社区居民提供免费健康检查和医疗咨询',
    fullDescription: '健康是最宝贵的财富。本次义诊活动邀请专业医护人员，为社区居民提供免费的血压测量、血糖检测、健康咨询等服务，普及健康知识，提高居民的健康意识。',
    image: 'https://plus.unsplash.com/premium_photo-1682339458664-ca534cd881d5?q=80&w=735&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D',
    category: '社区',
    date: '2024-01-22',
    location: '和谐社区',
    duration: '6小时',
    participants: 15,
    maxParticipants: 25,
    points: 60,
    status: '报名中',
    organizer: '爱心医疗志愿队'
  },
  {
    id: 5,
    title: '线上编程教学',
    description: '为偏远地区学生提供免费的编程启蒙教育',
    fullDescription: '科技改变未来，编程是21世纪的基本技能。本次线上教学活动将通过视频会议的方式，为偏远地区的学生提供免费的编程启蒙课程，帮助他们掌握基础的编程知识。',
    image: 'https://images.unsplash.com/photo-1565687981296-535f09db714e?q=80&w=1170&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D',
    category: '教育',
    date: '2024-01-25',
    location: '在线',
    duration: '2小时',
    participants: 30,
    maxParticipants: 50,
    points: 25,
    status: '进行中',
    organizer: '科技教育公益组织'
  },
  {
    id: 6,
    title: '植树造林活动',
    description: '种下希望的种子，为地球增添绿色',
    fullDescription: '植树造林是保护环境、改善生态的重要举措。本次活动将在郊区荒山进行植树，每位志愿者将亲手种下一棵树苗，为改善空气质量、防止水土流失贡献力量。',
    image: 'https://plus.unsplash.com/premium_photo-1681140560722-84cee06b48e4?q=80&w=687&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D',
    category: '环保',
    date: '2024-01-12',
    location: '郊区',
    duration: '8小时',
    participants: 50,
    maxParticipants: 50,
    points: 80,
    status: '已结束',
    organizer: '绿色未来环保协会'
  }
])

// 筛选后的活动
const filteredActivities = computed(() => {
  return activities.value.filter(activity => {
    const matchesSearch = activity.title.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
                         activity.description.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
                         activity.location.toLowerCase().includes(searchQuery.value.toLowerCase())
    
    const matchesCategory = !selectedCategory.value || activity.category === selectedCategory.value
    const matchesStatus = !selectedStatus.value || activity.status === selectedStatus.value
    const matchesLocation = !selectedLocation.value || 
                           activity.location.includes(selectedLocation.value) ||
                           (selectedLocation.value === '在线' && activity.location === '在线')
    
    return matchesSearch && matchesCategory && matchesStatus && matchesLocation
  })
})

// 方法
const formatDate = (dateString) => {
  const date = new Date(dateString)
  return date.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: 'long',
    day: 'numeric'
  })
}

const getStatusClass = (status) => {
  switch (status) {
    case '报名中': return 'status-open'
    case '进行中': return 'status-ongoing'
    case '已结束': return 'status-closed'
    default: return ''
  }
}

const getButtonText = (status) => {
  switch (status) {
    case '报名中': return '立即报名'
    case '进行中': return '进行中'
    case '已结束': return '已结束'
    default: return '立即报名'
  }
}

const selectActivity = (activity) => {
  selectedActivityDetail.value = activity
}

const closeModal = () => {
  selectedActivityDetail.value = null
}

const registerActivity = (activity) => {
  if (activity.status !== '报名中') return
  
  // 这里应该检查用户登录状态
  // 如果未登录，显示登录弹窗
  emit('show-login')
  
  // 模拟报名成功
  alert(`报名成功！您已成功报名参加「${activity.title}」活动。`)
  closeModal()
}
</script>

<style scoped>
.activities-page {
  min-height: 100vh;
  background: #f8f9fa;
}

.page-header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  text-align: center;
  padding: 60px 20px;
}

.page-header h1 {
  font-size: 2.5rem;
  margin-bottom: 10px;
}

.page-header p {
  font-size: 1.1rem;
  opacity: 0.9;
}

.search-section {
  background: white;
  padding: 30px 20px;
  box-shadow: 0 2px 10px rgba(0,0,0,0.1);
}

.search-container {
  max-width: 1200px;
  margin: 0 auto;
}

.search-bar {
  display: flex;
  margin-bottom: 20px;
  max-width: 600px;
  margin-left: auto;
  margin-right: auto;
}

.search-input {
  flex: 1;
  padding: 12px 20px;
  border: 2px solid #e1e5e9;
  border-radius: 25px 0 0 25px;
  font-size: 1rem;
  outline: none;
}

.search-input:focus {
  border-color: #667eea;
}

.search-btn {
  padding: 12px 20px;
  background: #667eea;
  color: white;
  border: none;
  border-radius: 0 25px 25px 0;
  cursor: pointer;
  font-size: 1rem;
}

.filters {
  display: flex;
  gap: 15px;
  justify-content: center;
  flex-wrap: wrap;
}

.filter-select {
  padding: 10px 15px;
  border: 1px solid #ddd;
  border-radius: 20px;
  background: white;
  cursor: pointer;
  outline: none;
}

.activities-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 40px 20px;
}

.activities-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 30px;
}

.activity-card {
  background: white;
  border-radius: 15px;
  overflow: hidden;
  box-shadow: 0 5px 15px rgba(0,0,0,0.08);
  transition: all 0.3s ease;
  cursor: pointer;
}

.activity-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 25px rgba(0,0,0,0.15);
}

.activity-image {
  position: relative;
  height: 200px;
  overflow: hidden;
}

.activity-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.activity-card:hover .activity-image img {
  transform: scale(1.05);
}

.activity-status {
  position: absolute;
  top: 10px;
  right: 10px;
  padding: 5px 12px;
  border-radius: 15px;
  font-size: 0.8rem;
  font-weight: 600;
}

.status-open {
  background: #4CAF50;
  color: white;
}

.status-ongoing {
  background: #FF9800;
  color: white;
}

.status-closed {
  background: #9E9E9E;
  color: white;
}

.activity-category {
  position: absolute;
  top: 10px;
  left: 10px;
  background: rgba(0,0,0,0.7);
  color: white;
  padding: 5px 10px;
  border-radius: 10px;
  font-size: 0.8rem;
}

.activity-content {
  padding: 20px;
}

.activity-title {
  font-size: 1.2rem;
  font-weight: 600;
  margin-bottom: 10px;
  color: #333;
}

.activity-description {
  color: #666;
  line-height: 1.5;
  margin-bottom: 15px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.activity-meta {
  margin-bottom: 15px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 5px;
  font-size: 0.9rem;
  color: #666;
}

.meta-icon {
  width: 16px;
}

.activity-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.participants-info {
  flex: 1;
}

.participants-count {
  font-size: 0.9rem;
  color: #666;
  margin-bottom: 5px;
  display: block;
}

.progress-bar {
  width: 100%;
  height: 6px;
  background: #e1e5e9;
  border-radius: 3px;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  background: linear-gradient(90deg, #667eea, #764ba2);
  transition: width 0.3s ease;
}

.activity-points {
  margin-left: 15px;
}

.points {
  background: #ff6b6b;
  color: white;
  padding: 5px 10px;
  border-radius: 12px;
  font-size: 0.8rem;
  font-weight: 600;
}

.btn-register {
  width: 100%;
  padding: 12px;
  border: none;
  border-radius: 8px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.btn-primary {
  background: #667eea;
  color: white;
}

.btn-primary:hover:not(:disabled) {
  background: #5a6fd8;
}

.btn-primary:disabled {
  background: #ccc;
  cursor: not-allowed;
}

.empty-state {
  text-align: center;
  padding: 60px 20px;
  color: #666;
}

.empty-icon {
  font-size: 4rem;
  margin-bottom: 20px;
}

.empty-state h3 {
  margin-bottom: 10px;
}

/* Modal Styles */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0,0,0,0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  padding: 20px;
}

.modal-content {
  background: white;
  border-radius: 15px;
  max-width: 600px;
  width: 100%;
  max-height: 90vh;
  overflow-y: auto;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  border-bottom: 1px solid #eee;
}

.modal-header h2 {
  margin: 0;
  color: #333;
}

.close-btn {
  background: none;
  border: none;
  font-size: 1.5rem;
  cursor: pointer;
  color: #666;
  padding: 5px;
}

.modal-body {
  padding: 20px;
}

.modal-image {
  width: 100%;
  height: 200px;
  object-fit: cover;
  border-radius: 10px;
  margin-bottom: 20px;
}

.detail-description {
  line-height: 1.6;
  color: #333;
  margin-bottom: 20px;
}

.detail-info {
  background: #f8f9fa;
  padding: 15px;
  border-radius: 10px;
  margin-bottom: 20px;
}

.info-row {
  margin-bottom: 10px;
  color: #333;
}

.info-row:last-child {
  margin-bottom: 0;
}

.organizer-info {
  border-top: 1px solid #eee;
  padding-top: 15px;
}

.organizer-info h4 {
  margin-bottom: 10px;
  color: #333;
}

.modal-footer {
  display: flex;
  gap: 10px;
  justify-content: flex-end;
  padding: 20px;
  border-top: 1px solid #eee;
}

.btn {
  padding: 10px 20px;
  border: none;
  border-radius: 8px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.btn-secondary {
  background: #6c757d;
  color: white;
}

.btn-secondary:hover {
  background: #5a6268;
}

/* Responsive */
@media (max-width: 768px) {
  .activities-grid {
    grid-template-columns: 1fr;
  }
  
  .search-bar {
    flex-direction: column;
  }
  
  .search-input {
    border-radius: 25px;
    margin-bottom: 10px;
  }
  
  .search-btn {
    border-radius: 25px;
  }
  
  .filters {
    flex-direction: column;
    align-items: center;
  }
  
  .filter-select {
    width: 200px;
  }
  
  .activity-footer {
    flex-direction: column;
    gap: 10px;
    align-items: stretch;
  }
  
  .modal-content {
    margin: 10px;
    max-height: calc(100vh - 20px);
  }
}
</style>