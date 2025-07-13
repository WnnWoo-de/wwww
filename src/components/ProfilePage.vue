<template>
  <div class="profile-page">
    <!-- 加载状态 -->
    <div v-if="isLoading" class="loading-container">
      <div class="loading-spinner"></div>
      <p class="loading-text">正在加载个人信息...</p>
    </div>
    
    <!-- 错误状态 -->
    <div v-else-if="error" class="error-container">
      <div class="error-icon">⚠️</div>
      <p class="error-text">{{ error }}</p>
      <button class="retry-btn" @click="initializeData">重新加载</button>
    </div>
    
    <!-- 主要内容 -->
    <div v-else class="profile-content-wrapper">
    <!-- Profile Header -->
    <div class="profile-header">
      <div class="header-background"></div>
      <div class="profile-content">
        <div class="avatar-section">
          <div class="avatar-container">
            <img :src="userProfile.avatar" :alt="userProfile.name" class="user-avatar">
            <button class="edit-avatar-btn" @click="editAvatar">
              <span>📷</span>
            </button>
          </div>
          
          <div class="user-info">
            <h1 class="user-name">{{ userProfile.name }}</h1>
            <p class="user-title">{{ userProfile.title }}</p>
            <div class="user-stats">
              <div class="stat-item">
                <span class="stat-number">{{ userProfile.totalHours }}</span>
                <span class="stat-label">志愿时长</span>
              </div>
              <div class="stat-item">
                <span class="stat-number">{{ userProfile.activitiesCount }}</span>
                <span class="stat-label">参与活动</span>
              </div>
              <div class="stat-item">
                <span class="stat-number">{{ userProfile.points }}</span>
                <span class="stat-label">积分余额</span>
              </div>
            </div>
            
            <div class="user-meta">
              <div class="meta-item">
                <span class="meta-icon">📅</span>
                <span class="meta-text">加入时间：{{ formatJoinDate(userProfile.joinDate) }}</span>
              </div>
              <div class="meta-item">
                <span class="meta-icon">📍</span>
                <span class="meta-text">所在地区：{{ userProfile.location }}</span>
              </div>
            </div>
          </div>
        </div>
        
        <div class="level-section">
          <div class="level-info">
            <div class="level-badge">
              <span class="level-icon">{{ getLevelIcon(userProfile.level) }}</span>
              <span class="level-text">{{ getLevelName(userProfile.level) }}</span>
            </div>
            <div class="level-progress">
              <div class="progress-bar">
                <div 
                  class="progress-fill" 
                  :style="{ width: getLevelProgress() + '%' }"
                ></div>
              </div>
              <div class="progress-text">
                {{ userProfile.currentLevelHours }}/{{ getNextLevelHours() }} 小时
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Navigation Tabs -->
    <div class="tabs-section">
      <div class="tabs-container">
        <button 
          v-for="tab in tabs" 
          :key="tab.id"
          :class="['tab-btn', { active: activeTab === tab.id }]"
          @click="activeTab = tab.id"
        >
          <span class="tab-icon">{{ tab.icon }}</span>
          <span class="tab-text">{{ tab.name }}</span>
        </button>
      </div>
    </div>

    <!-- Tab Content -->
    <div class="content-section">
      <div class="content-container">
        <!-- 个人资料 -->
        <div v-if="activeTab === 'profile'" class="tab-content">
          <div class="profile-overview">
            <div class="overview-header">
              <h2>个人资料概览</h2>
              <p>管理您的个人信息和志愿服务档案</p>
            </div>
            
            <div class="profile-cards">
              <div class="profile-card">
                <div class="card-header">
                  <h3>基本信息</h3>
                  <button class="edit-profile-btn" @click="editProfile">编辑</button>
                </div>
                <div class="card-content">
                  <div class="info-row">
                    <span class="info-label">姓名</span>
                    <span class="info-value">{{ userProfile.name }}</span>
                  </div>
                  <div class="info-row">
                    <span class="info-label">称号</span>
                    <span class="info-value">{{ userProfile.title }}</span>
                  </div>
                  <div class="info-row">
                    <span class="info-label">个人简介</span>
                    <span class="info-value">{{ userProfile.bio }}</span>
                  </div>
                  <div class="info-row">
                    <span class="info-label">联系方式</span>
                    <span class="info-value">{{ userProfile.contact }}</span>
                  </div>
                  <div class="info-row">
                    <span class="info-label">所在地区</span>
                    <span class="info-value">{{ userProfile.location }}</span>
                  </div>
                  <div class="info-row">
                    <span class="info-label">加入时间</span>
                    <span class="info-value">{{ formatJoinDate(userProfile.joinDate) }}</span>
                  </div>
                </div>
              </div>
              
              <div class="profile-card">
                <div class="card-header">
                  <h3>志愿服务统计</h3>
                </div>
                <div class="card-content">
                  <div class="stats-grid">
                    <div class="stat-box">
                      <div class="stat-icon">⏰</div>
                      <div class="stat-info">
                        <div class="stat-number">{{ userProfile.totalHours }}</div>
                        <div class="stat-label">总志愿时长</div>
                      </div>
                    </div>
                    <div class="stat-box">
                      <div class="stat-icon">🎯</div>
                      <div class="stat-info">
                        <div class="stat-number">{{ userProfile.activitiesCount }}</div>
                        <div class="stat-label">参与活动</div>
                      </div>
                    </div>
                    <div class="stat-box">
                      <div class="stat-icon">💎</div>
                      <div class="stat-info">
                        <div class="stat-number">{{ userProfile.points }}</div>
                        <div class="stat-label">当前积分</div>
                      </div>
                    </div>
                    <div class="stat-box">
                      <div class="stat-icon">🏆</div>
                      <div class="stat-info">
                        <div class="stat-number">{{ getLevelName(userProfile.level) }}</div>
                        <div class="stat-label">当前等级</div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
              
              <div class="profile-card">
                <div class="card-header">
                  <h3>最近活动</h3>
                  <button class="view-all-btn" @click="activeTab = 'activities'">查看全部</button>
                </div>
                <div class="card-content">
                  <div class="recent-activities">
                    <div 
                      v-for="activity in userActivities.slice(0, 3)" 
                      :key="activity.id" 
                      class="recent-activity"
                    >
                      <div class="activity-icon">
                        <span>{{ getActivityIcon(activity.status) }}</span>
                      </div>
                      <div class="activity-info">
                        <div class="activity-name">{{ activity.title }}</div>
                        <div class="activity-meta">{{ formatTime(activity.date) }} · {{ activity.duration }}</div>
                      </div>
                      <div class="activity-status">
                        <span :class="['status-badge', getStatusClass(activity.status)]">{{ activity.status }}</span>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
        
        <!-- 活动记录 -->
        <div v-if="activeTab === 'activities'" class="tab-content">
          <div class="activities-list">
            <div 
              v-for="activity in userActivities" 
              :key="activity.id" 
              class="activity-record"
            >
              <div class="activity-date">
                <div class="date-day">{{ formatDay(activity.date) }}</div>
                <div class="date-month">{{ formatMonth(activity.date) }}</div>
              </div>
              
              <div class="activity-content">
                <div class="activity-header">
                  <h3 class="activity-title">{{ activity.title }}</h3>
                  <span :class="['activity-status', getStatusClass(activity.status)]">
                    {{ activity.status }}
                  </span>
                </div>
                
                <div class="activity-details">
                  <div class="detail-item">
                    <span class="detail-icon">📍</span>
                    <span class="detail-text">{{ activity.location }}</span>
                  </div>
                  <div class="detail-item">
                    <span class="detail-icon">⏰</span>
                    <span class="detail-text">{{ activity.duration }}</span>
                  </div>
                  <div class="detail-item">
                    <span class="detail-icon">💎</span>
                    <span class="detail-text">+{{ activity.points }} 积分</span>
                  </div>
                </div>
                
                <p class="activity-description">{{ activity.description }}</p>
                
                <div v-if="activity.certificate" class="certificate-section">
                  <button class="certificate-btn" @click="viewCertificate(activity)">
                    <span class="cert-icon">🏆</span>
                    <span>查看证书</span>
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 成长树 -->
        <div v-if="activeTab === 'growth'" class="tab-content">
          <div class="growth-tree">
            <div class="tree-header">
              <h2>我的成长树</h2>
              <p>每一次志愿服务都是成长的足迹</p>
            </div>
            
            <div class="tree-visualization">
              <div class="tree-trunk">
                <div class="trunk-line"></div>
                
                <div 
                  v-for="(milestone, index) in growthMilestones" 
                  :key="milestone.id" 
                  :class="['milestone', { achieved: milestone.achieved, current: milestone.current }]"
                  :style="{ bottom: (index * 120 + 50) + 'px' }"
                >
                  <div class="milestone-icon">
                    <span>{{ milestone.icon }}</span>
                  </div>
                  <div class="milestone-content">
                    <h4 class="milestone-title">{{ milestone.title }}</h4>
                    <p class="milestone-description">{{ milestone.description }}</p>
                    <div class="milestone-requirement">
                      {{ milestone.requirement }}
                    </div>
                  </div>
                  
                  <div v-if="milestone.achieved" class="achievement-badge">
                    <span>✓</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 积分记录 -->
        <div v-if="activeTab === 'points'" class="tab-content">
          <div class="points-overview">
            <div class="points-summary">
              <div class="summary-card">
                <div class="card-icon">💎</div>
                <div class="card-content">
                  <div class="card-number">{{ userProfile.points }}</div>
                  <div class="card-label">当前积分</div>
                </div>
              </div>
              
              <div class="summary-card">
                <div class="card-icon">📈</div>
                <div class="card-content">
                  <div class="card-number">{{ userProfile.totalEarned }}</div>
                  <div class="card-label">累计获得</div>
                </div>
              </div>
              
              <div class="summary-card">
                <div class="card-icon">🛍️</div>
                <div class="card-content">
                  <div class="card-number">{{ userProfile.totalSpent }}</div>
                  <div class="card-label">累计消费</div>
                </div>
              </div>
            </div>
            
            <div class="points-history">
              <h3>积分记录</h3>
              <div class="history-list">
                <div 
                  v-for="record in pointsHistory" 
                  :key="record.id" 
                  class="history-item"
                >
                  <div class="history-icon">
                    <span :class="['icon', record.type === 'earn' ? 'earn' : 'spend']">{{ record.icon }}</span>
                  </div>
                  
                  <div class="history-content">
                    <div class="history-title">{{ record.title }}</div>
                    <div class="history-time">{{ formatTime(record.time) }}</div>
                  </div>
                  
                  <div class="history-points">
                    <span :class="['points', record.type === 'earn' ? 'earn' : 'spend']">
                      {{ record.type === 'earn' ? '+' : '-' }}{{ record.points }}
                    </span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 设置 -->
        <div v-if="activeTab === 'settings'" class="tab-content">
          <div class="settings-section">
            <div class="settings-group">
              <h3>个人信息</h3>
              <div class="setting-item">
                <label class="setting-label">昵称</label>
                <div class="setting-control">
                  <input v-model="editableProfile.name" type="text" class="setting-input">
                  <button class="edit-btn">编辑</button>
                </div>
              </div>
              
              <div class="setting-item">
                <label class="setting-label">个人简介</label>
                <div class="setting-control">
                  <textarea v-model="editableProfile.bio" class="setting-textarea" rows="3"></textarea>
                </div>
              </div>
              
              <div class="setting-item">
                <label class="setting-label">联系方式</label>
                <div class="setting-control">
                  <input v-model="editableProfile.contact" type="text" class="setting-input">
                </div>
              </div>
            </div>
            
            <div class="settings-group">
              <h3>通知设置</h3>
              <div class="setting-item">
                <label class="setting-label">活动提醒</label>
                <div class="setting-control">
                  <label class="switch">
                    <input v-model="notificationSettings.activities" type="checkbox">
                    <span class="slider"></span>
                  </label>
                </div>
              </div>
              
              <div class="setting-item">
                <label class="setting-label">积分变动</label>
                <div class="setting-control">
                  <label class="switch">
                    <input v-model="notificationSettings.points" type="checkbox">
                    <span class="slider"></span>
                  </label>
                </div>
              </div>
              
              <div class="setting-item">
                <label class="setting-label">社区动态</label>
                <div class="setting-control">
                  <label class="switch">
                    <input v-model="notificationSettings.community" type="checkbox">
                    <span class="slider"></span>
                  </label>
                </div>
              </div>
            </div>
            
            <div class="settings-group">
              <h3>隐私设置</h3>
              <div class="setting-item">
                <label class="setting-label">公开个人资料</label>
                <div class="setting-control">
                  <label class="switch">
                    <input v-model="privacySettings.publicProfile" type="checkbox">
                    <span class="slider"></span>
                  </label>
                </div>
              </div>
              
              <div class="setting-item">
                <label class="setting-label">显示活动记录</label>
                <div class="setting-control">
                  <label class="switch">
                    <input v-model="privacySettings.showActivities" type="checkbox">
                    <span class="slider"></span>
                  </label>
                </div>
              </div>
            </div>
            
            <div class="settings-actions">
              <button class="btn btn-primary" @click="saveSettings">保存设置</button>
              <button class="btn btn-secondary" @click="resetSettings">重置</button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Certificate Modal -->
    <div v-if="showCertificate" class="modal-overlay" @click="closeCertificate">
      <div class="certificate-modal" @click.stop>
        <div class="certificate-header">
          <h2>志愿服务证书</h2>
          <button class="close-btn" @click="closeCertificate">×</button>
        </div>
        
        <div class="certificate-content">
          <div class="certificate-design">
            <div class="cert-border">
              <div class="cert-title">志愿服务证书</div>
              <div class="cert-subtitle">Certificate of Volunteer Service</div>
              
              <div class="cert-body">
                <p class="cert-text">兹证明</p>
                <p class="cert-name">{{ userProfile.name }}</p>
                <p class="cert-text">于 {{ formatCertDate(selectedActivity?.date) }} 参加了</p>
                <p class="cert-activity">{{ selectedActivity?.title }}</p>
                <p class="cert-text">志愿服务活动，服务时长 {{ selectedActivity?.duration }}，表现优秀。</p>
                <p class="cert-text">特此证明。</p>
              </div>
              
              <div class="cert-footer">
                <div class="cert-date">{{ formatCertDate(new Date()) }}</div>
                <div class="cert-seal">🏆</div>
              </div>
            </div>
          </div>
          
          <div class="certificate-actions">
            <button class="btn btn-primary" @click="downloadCertificate">下载证书</button>
            <button class="btn btn-secondary" @click="shareCertificate">分享证书</button>
          </div>
        </div>
      </div>
    </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, reactive } from 'vue';

const isLoading = ref(true);
const error = ref(null);
const activeTab = ref('profile');
const editableProfile = ref({});

const userProfile = ref({});

const tabs = ref([
  { id: 'profile', name: '个人资料', icon: '👤' },
  { id: 'activities', name: '活动记录', icon: '📋' },
  { id: 'growth', name: '成长树', icon: '🌱' },
  { id: 'points', name: '积分记录', icon: '💎' },
  { id: 'settings', name: '设置', icon: '⚙️' }
]);



const initializeData = () => {
  isLoading.value = true;
  error.value = null;
  const mockProfile = {
    name: '张志愿',
    title: '公益达人',
    bio: '热爱公益事业，致力于帮助他人，传递正能量。',
    contact: 'zhangzhiyuan@example.com',
    location: '北京市朝阳区',
    joinDate: '2023-03-15',
    avatar: 'https://c-ssl.duitang.com/uploads/blog/202211/25/20221125183404_68433.jpeg',
    totalHours: 128,
    activitiesCount: 15,
    points: 1280,
    totalEarned: 2080,
    totalSpent: 800,
    level: 3,
    currentLevelHours: 128
  };
  userProfile.value = mockProfile;
  editableProfile.value = { ...mockProfile };
  isLoading.value = false;
};

onMounted(() => {
  // 直接初始化数据，无需登录检查
  initializeData();
});

// 证书相关
const showCertificate = ref(false)
const selectedActivity = ref(null)



// 可编辑的资料


// 通知设置
const notificationSettings = reactive({
  activities: true,
  points: true,
  community: false
})

// 隐私设置
const privacySettings = reactive({
  publicProfile: true,
  showActivities: true
})

// 用户活动记录
const userActivities = ref([
  {
    id: 1,
    title: '环保清洁行动',
    date: '2024-01-15',
    location: '滨江公园',
    duration: '4小时',
    points: 40,
    status: '已完成',
    description: '参与公园环境清洁，清理垃圾，维护环境卫生。',
    certificate: true
  },
  {
    id: 2,
    title: '关爱空巢老人志愿服务',
    date: '2024-01-10',
    location: '阳光社区',
    duration: '3小时',
    points: 30,
    status: '已完成',
    description: '陪伴社区独居老人，为他们带去温暖和关怀。',
    certificate: true
  },
  {
    id: 3,
    title: '儿童阅读推广活动',
    date: '2024-01-20',
    location: '希望小学',
    duration: '5小时',
    points: 50,
    status: '已报名',
    description: '为山区儿童带去知识的力量和阅读的快乐。',
    certificate: false
  }
])

// 成长里程碑
const growthMilestones = ref([
  {
    id: 1,
    title: '志愿新手',
    description: '完成第一次志愿服务',
    requirement: '1小时志愿时长',
    icon: '🌱',
    achieved: true,
    current: false
  },
  {
    id: 2,
    title: '爱心使者',
    description: '累计志愿服务50小时',
    requirement: '50小时志愿时长',
    icon: '💝',
    achieved: true,
    current: false
  },
  {
    id: 3,
    title: '公益达人',
    description: '累计志愿服务100小时',
    requirement: '100小时志愿时长',
    icon: '🏆',
    achieved: true,
    current: true
  },
  {
    id: 4,
    title: '志愿之星',
    description: '累计志愿服务200小时',
    requirement: '200小时志愿时长',
    icon: '⭐',
    achieved: false,
    current: false
  },
  {
    id: 5,
    title: '公益领袖',
    description: '累计志愿服务500小时',
    requirement: '500小时志愿时长',
    icon: '👑',
    achieved: false,
    current: false
  }
])

// 积分记录
const pointsHistory = ref([
  {
    id: 1,
    title: '参与环保清洁行动',
    type: 'earn',
    points: 40,
    time: '2024-01-15T16:00:00Z',
    icon: '🌱'
  },
  {
    id: 2,
    title: '兑换蓝牙耳机',
    type: 'spend',
    points: 800,
    time: '2024-01-14T10:30:00Z',
    icon: '🛍️'
  },
  {
    id: 3,
    title: '关爱老人志愿服务',
    type: 'earn',
    points: 30,
    time: '2024-01-10T14:00:00Z',
    icon: '❤️'
  },
  {
    id: 4,
    title: '兑换图书套装',
    type: 'spend',
    points: 300,
    time: '2024-01-08T09:15:00Z',
    icon: '📚'
  }
])

// 计算属性和方法
const getLevelIcon = (level) => {
  const icons = ['🌱', '💝', '🏆', '⭐', '👑']
  return icons[level - 1] || '🌱'
}

const getLevelName = (level) => {
  const names = ['志愿新手', '爱心使者', '公益达人', '志愿之星', '公益领袖']
  return names[level - 1] || '志愿新手'
}

const getNextLevelHours = () => {
  const requirements = [1, 50, 100, 200, 500]
  return requirements[userProfile.level] || 1000
}

const getLevelProgress = () => {
  const nextLevelHours = getNextLevelHours()
  return Math.min((userProfile.currentLevelHours / nextLevelHours) * 100, 100)
}

const formatDay = (dateString) => {
  return new Date(dateString).getDate().toString().padStart(2, '0')
}

const formatMonth = (dateString) => {
  const months = ['1月', '2月', '3月', '4月', '5月', '6月', '7月', '8月', '9月', '10月', '11月', '12月']
  return months[new Date(dateString).getMonth()]
}

const formatTime = (dateString) => {
  return new Date(dateString).toLocaleDateString('zh-CN')
}

const formatCertDate = (date) => {
  return new Date(date).toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: 'long',
    day: 'numeric'
  })
}

const formatJoinDate = (dateString) => {
  return new Date(dateString).toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: 'long'
  })
}

const getStatusClass = (status) => {
  switch (status) {
    case '已完成': return 'completed'
    case '已报名': return 'registered'
    case '进行中': return 'ongoing'
    default: return ''
  }
}

const getActivityIcon = (status) => {
  switch (status) {
    case '已完成': return '✅'
    case '已报名': return '📝'
    case '进行中': return '🔄'
    default: return '📋'
  }
}

// 方法
const editAvatar = () => {
  // 模拟编辑头像
  alert('编辑头像功能')
}

const editProfile = () => {
  // 跳转到设置页面
  activeTab.value = 'settings'
}

const viewCertificate = (activity) => {
  selectedActivity.value = activity
  showCertificate.value = true
}

const closeCertificate = () => {
  showCertificate.value = false
  selectedActivity.value = null
}

const downloadCertificate = () => {
  alert('下载证书功能')
}

const shareCertificate = () => {
  alert('分享证书功能')
}

// 初始化数据已在上方定义

const saveSettings = async () => {
  try {
    isLoading.value = true
    
    // 数据验证
    if (!editableProfile.name.trim()) {
      alert('昵称不能为空')
      return
    }
    
    if (editableProfile.contact && !isValidEmail(editableProfile.contact)) {
      alert('请输入有效的邮箱地址')
      return
    }
    
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 500))
    
    // 保存设置
    Object.assign(userProfile, editableProfile)
    alert('设置已保存')
  } catch (err) {
    alert('保存失败，请稍后重试')
    console.error('保存设置失败:', err)
  } finally {
    isLoading.value = false
  }
}

const resetSettings = () => {
  // 重置设置
  Object.assign(editableProfile, userProfile)
  alert('设置已重置')
}

// 邮箱验证
const isValidEmail = (email) => {
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  return emailRegex.test(email)
}

// 组件挂载逻辑已在上方定义
</script>

<style scoped>
.profile-page {
  min-height: 100vh;
  background: #f8f9fa;
}

/* 加载状态样式 */
.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 60vh;
  padding: 40px;
}

.loading-spinner {
  width: 50px;
  height: 50px;
  border: 4px solid #f3f3f3;
  border-top: 4px solid #667eea;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 20px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.loading-text {
  color: #666;
  font-size: 1.1rem;
  margin: 0;
}

/* 错误状态样式 */
.error-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 60vh;
  padding: 40px;
  text-align: center;
}

.error-icon {
  font-size: 4rem;
  margin-bottom: 20px;
}

.error-text {
  color: #dc3545;
  font-size: 1.2rem;
  margin-bottom: 30px;
  max-width: 400px;
  line-height: 1.5;
}

.retry-btn {
  padding: 12px 24px;
  background: #667eea;
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.retry-btn:hover {
  background: #5a6fd8;
  transform: translateY(-2px);
}

.profile-header {
  position: relative;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 40px 20px;
}

.header-background {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: url('data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100 100"><defs><pattern id="grain" width="100" height="100" patternUnits="userSpaceOnUse"><circle cx="50" cy="50" r="1" fill="%23ffffff" opacity="0.1"/></pattern></defs><rect width="100" height="100" fill="url(%23grain)"/></svg>') repeat;
}

.profile-content {
  position: relative;
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 40px;
}

.avatar-section {
  display: flex;
  align-items: center;
  gap: 30px;
}

.avatar-container {
  position: relative;
}

.user-avatar {
  width: 120px;
  height: 120px;
  border-radius: 50%;
  object-fit: cover;
  border: 4px solid rgba(255,255,255,0.3);
}

.edit-avatar-btn {
  position: absolute;
  bottom: 5px;
  right: 5px;
  width: 35px;
  height: 35px;
  border: none;
  background: rgba(255,255,255,0.9);
  border-radius: 50%;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1rem;
}

.user-info {
  flex: 1;
}

.user-name {
  font-size: 2.5rem;
  font-weight: 700;
  margin-bottom: 5px;
}

.user-title {
  font-size: 1.2rem;
  opacity: 0.9;
  margin-bottom: 20px;
}

.user-stats {
  display: flex;
  gap: 30px;
}

.stat-item {
  text-align: center;
}

.stat-number {
  display: block;
  font-size: 2rem;
  font-weight: 700;
  margin-bottom: 5px;
}

.stat-label {
  font-size: 0.9rem;
  opacity: 0.8;
}

.user-meta {
  margin-top: 20px;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 0.95rem;
  opacity: 0.9;
}

.meta-icon {
  font-size: 1rem;
}

.meta-text {
  color: rgba(255, 255, 255, 0.9);
}

.level-section {
  background: rgba(255,255,255,0.1);
  padding: 20px;
  border-radius: 15px;
  backdrop-filter: blur(10px);
  min-width: 300px;
}

.level-info {
  text-align: center;
}

.level-badge {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  margin-bottom: 15px;
}

.level-icon {
  font-size: 2rem;
}

.level-text {
  font-size: 1.3rem;
  font-weight: 600;
}

.level-progress {
  width: 100%;
}

.progress-bar {
  width: 100%;
  height: 8px;
  background: rgba(255,255,255,0.3);
  border-radius: 4px;
  overflow: hidden;
  margin-bottom: 8px;
}

.progress-fill {
  height: 100%;
  background: #4CAF50;
  transition: width 0.3s ease;
}

.progress-text {
  font-size: 0.9rem;
  opacity: 0.9;
}

.tabs-section {
  background: white;
  border-bottom: 1px solid #eee;
  padding: 0 20px;
}

.tabs-container {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  gap: 0;
}

.tab-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 20px 30px;
  border: none;
  background: none;
  cursor: pointer;
  border-bottom: 3px solid transparent;
  transition: all 0.3s ease;
  color: #666;
}

.tab-btn:hover {
  color: #667eea;
  background: #f8f9fa;
}

.tab-btn.active {
  color: #667eea;
  border-bottom-color: #667eea;
}

.tab-icon {
  font-size: 1.2rem;
}

.tab-text {
  font-weight: 600;
}

.content-section {
  padding: 40px 20px;
}

.content-container {
  max-width: 1200px;
  margin: 0 auto;
}

.tab-content {
  background: white;
  border-radius: 15px;
  padding: 30px;
  box-shadow: 0 2px 10px rgba(0,0,0,0.08);
}

/* Profile Tab */
.profile-overview {
  max-width: 100%;
}

.overview-header {
  text-align: center;
  margin-bottom: 40px;
}

.overview-header h2 {
  font-size: 2rem;
  color: #333;
  margin-bottom: 10px;
}

.overview-header p {
  color: #666;
  font-size: 1.1rem;
}

.profile-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(350px, 1fr));
  gap: 25px;
}

.profile-card {
  background: #f8f9fa;
  border-radius: 15px;
  overflow: hidden;
  box-shadow: 0 2px 10px rgba(0,0,0,0.05);
  transition: all 0.3s ease;
}

.profile-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(0,0,0,0.1);
}

.card-header {
  background: white;
  padding: 20px;
  border-bottom: 1px solid #eee;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header h3 {
  margin: 0;
  color: #333;
  font-size: 1.3rem;
}

.edit-profile-btn,
.view-all-btn {
  padding: 6px 12px;
  border: 1px solid #667eea;
  background: white;
  color: #667eea;
  border-radius: 6px;
  cursor: pointer;
  font-size: 0.9rem;
  font-weight: 500;
  transition: all 0.3s ease;
}

.edit-profile-btn:hover,
.view-all-btn:hover {
  background: #667eea;
  color: white;
}

.card-content {
  padding: 20px;
}

.info-row {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  padding: 12px 0;
  border-bottom: 1px solid #eee;
}

.info-row:last-child {
  border-bottom: none;
}

.info-label {
  font-weight: 600;
  color: #555;
  min-width: 80px;
}

.info-value {
  color: #333;
  text-align: right;
  flex: 1;
  margin-left: 20px;
  word-break: break-word;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 15px;
}

.stat-box {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 15px;
  background: white;
  border-radius: 10px;
  border: 1px solid #eee;
}

.stat-box .stat-icon {
  font-size: 1.8rem;
  width: 40px;
  text-align: center;
}

.stat-box .stat-info {
  flex: 1;
}

.stat-box .stat-number {
  font-size: 1.3rem;
  font-weight: 700;
  color: #333;
  margin-bottom: 2px;
}

.stat-box .stat-label {
  font-size: 0.85rem;
  color: #666;
}

.recent-activities {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.recent-activity {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  background: white;
  border-radius: 8px;
  border: 1px solid #eee;
  transition: all 0.3s ease;
}

.recent-activity:hover {
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
}

.recent-activity .activity-icon {
  width: 35px;
  height: 35px;
  border-radius: 50%;
  background: #f0f0f0;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.1rem;
}

.recent-activity .activity-info {
  flex: 1;
}

.recent-activity .activity-name {
  font-weight: 600;
  color: #333;
  margin-bottom: 4px;
}

.recent-activity .activity-meta {
  font-size: 0.85rem;
  color: #666;
}

.recent-activity .status-badge {
  padding: 4px 8px;
  border-radius: 12px;
  font-size: 0.75rem;
  font-weight: 600;
}

/* Activities Tab */
.activities-list {
  display: flex;
  flex-direction: column;
  gap: 25px;
}

.activity-record {
  display: flex;
  gap: 20px;
  padding: 20px;
  border: 1px solid #eee;
  border-radius: 12px;
  transition: all 0.3s ease;
}

.activity-record:hover {
  box-shadow: 0 5px 15px rgba(0,0,0,0.1);
}

.activity-date {
  text-align: center;
  min-width: 60px;
}

.date-day {
  font-size: 2rem;
  font-weight: 700;
  color: #667eea;
  line-height: 1;
}

.date-month {
  font-size: 0.9rem;
  color: #666;
  margin-top: 5px;
}

.activity-content {
  flex: 1;
}

.activity-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 10px;
}

.activity-title {
  font-size: 1.2rem;
  font-weight: 600;
  color: #333;
  margin: 0;
}

.activity-status {
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 0.8rem;
  font-weight: 600;
}

.activity-status.completed {
  background: #d4edda;
  color: #155724;
}

.activity-status.registered {
  background: #cce5ff;
  color: #004085;
}

.activity-status.ongoing {
  background: #fff3cd;
  color: #856404;
}

.activity-details {
  display: flex;
  gap: 20px;
  margin-bottom: 10px;
  flex-wrap: wrap;
}

.detail-item {
  display: flex;
  align-items: center;
  gap: 5px;
  font-size: 0.9rem;
  color: #666;
}

.detail-icon {
  width: 16px;
}

.activity-description {
  color: #666;
  line-height: 1.5;
  margin-bottom: 15px;
}

.certificate-section {
  margin-top: 15px;
}

.certificate-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  border: 1px solid #667eea;
  background: white;
  color: #667eea;
  border-radius: 20px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.certificate-btn:hover {
  background: #667eea;
  color: white;
}

/* Growth Tree Tab */
.growth-tree {
  text-align: center;
}

.tree-header {
  margin-bottom: 40px;
}

.tree-header h2 {
  font-size: 2rem;
  color: #333;
  margin-bottom: 10px;
}

.tree-header p {
  color: #666;
  font-size: 1.1rem;
}

.tree-visualization {
  position: relative;
  margin: 0 auto;
  max-width: 800px;
  padding: 20px;
}

.tree-trunk {
  position: relative;
  display: flex;
  flex-direction: column;
  gap: 40px;
}

.trunk-line {
  position: absolute;
  left: 50%;
  top: 0;
  bottom: 0;
  width: 4px;
  background: linear-gradient(to bottom, #4CAF50, #8BC34A);
  transform: translateX(-50%);
  z-index: 1;
}

.milestone {
  position: relative;
  display: flex;
  align-items: center;
  gap: 20px;
  width: 100%;
  z-index: 2;
}

.milestone:nth-child(even) {
  flex-direction: row-reverse;
}

.milestone:nth-child(even) .milestone-content {
  text-align: right;
}

.milestone.achieved .milestone-icon {
  background: #4CAF50;
  color: white;
  box-shadow: 0 0 20px rgba(76, 175, 80, 0.3);
}

.milestone.current .milestone-icon {
  background: #FF9800;
  color: white;
  animation: pulse 2s infinite;
  box-shadow: 0 0 20px rgba(255, 152, 0, 0.4);
}

.milestone-icon {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  background: #e0e0e0;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 2rem;
  position: relative;
  z-index: 3;
  border: 4px solid white;
  box-shadow: 0 4px 15px rgba(0,0,0,0.1);
  transition: all 0.3s ease;
  flex-shrink: 0;
}

.milestone-icon:hover {
  transform: scale(1.1);
}

.milestone-content {
  flex: 1;
  text-align: left;
  background: white;
  padding: 20px 25px;
  border-radius: 15px;
  box-shadow: 0 4px 20px rgba(0,0,0,0.08);
  border: 1px solid #f0f0f0;
  transition: all 0.3s ease;
  max-width: 350px;
}

.milestone-content:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(0,0,0,0.12);
}

.milestone-title {
  font-size: 1.3rem;
  font-weight: 700;
  color: #333;
  margin-bottom: 8px;
}

.milestone-description {
  color: #666;
  margin-bottom: 10px;
  font-size: 1rem;
  line-height: 1.5;
}

.milestone-requirement {
  font-size: 0.9rem;
  color: #999;
  font-weight: 500;
  padding: 8px 12px;
  background: #f8f9fa;
  border-radius: 8px;
  display: inline-block;
}

.achievement-badge {
  position: absolute;
  top: -8px;
  right: -8px;
  width: 32px;
  height: 32px;
  background: #4CAF50;
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1rem;
  font-weight: 700;
  border: 3px solid white;
  box-shadow: 0 2px 10px rgba(76, 175, 80, 0.3);
}

@keyframes pulse {
  0% { transform: scale(1); box-shadow: 0 0 20px rgba(255, 152, 0, 0.4); }
  50% { transform: scale(1.05); box-shadow: 0 0 30px rgba(255, 152, 0, 0.6); }
  100% { transform: scale(1); box-shadow: 0 0 20px rgba(255, 152, 0, 0.4); }
}

/* Points Tab */
.points-summary {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
  margin-bottom: 40px;
}

.summary-card {
  display: flex;
  align-items: center;
  gap: 15px;
  padding: 20px;
  background: linear-gradient(135deg, #f8f9fa, #e9ecef);
  border-radius: 12px;
}

.card-icon {
  font-size: 2.5rem;
}

.card-content {
  flex: 1;
}

.card-number {
  font-size: 2rem;
  font-weight: 700;
  color: #333;
  margin-bottom: 5px;
}

.card-label {
  color: #666;
  font-size: 0.9rem;
}

.points-history h3 {
  margin-bottom: 20px;
  color: #333;
}

.history-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.history-item {
  display: flex;
  align-items: center;
  gap: 15px;
  padding: 15px;
  border: 1px solid #eee;
  border-radius: 10px;
}

.history-icon {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.2rem;
}

.history-icon .icon.earn {
  background: #d4edda;
  color: #155724;
}

.history-icon .icon.spend {
  background: #f8d7da;
  color: #721c24;
}

.history-content {
  flex: 1;
}

.history-title {
  font-weight: 600;
  color: #333;
  margin-bottom: 5px;
}

.history-time {
  font-size: 0.9rem;
  color: #666;
}

.history-points {
  font-weight: 700;
  font-size: 1.1rem;
}

.history-points .points.earn {
  color: #28a745;
}

.history-points .points.spend {
  color: #dc3545;
}

/* Settings Tab */
.settings-section {
  max-width: 600px;
}

.settings-group {
  margin-bottom: 40px;
}

.settings-group h3 {
  color: #333;
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 2px solid #f0f0f0;
}

.setting-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 0;
  border-bottom: 1px solid #f0f0f0;
}

.setting-item:last-child {
  border-bottom: none;
}

.setting-label {
  font-weight: 600;
  color: #333;
  flex: 1;
}

.setting-control {
  display: flex;
  align-items: center;
  gap: 10px;
}

.setting-input {
  padding: 8px 12px;
  border: 1px solid #ddd;
  border-radius: 6px;
  outline: none;
  width: 200px;
}

.setting-textarea {
  padding: 8px 12px;
  border: 1px solid #ddd;
  border-radius: 6px;
  outline: none;
  width: 300px;
  resize: vertical;
  font-family: inherit;
}

.edit-btn {
  padding: 6px 12px;
  border: 1px solid #667eea;
  background: white;
  color: #667eea;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.9rem;
}

.edit-btn:hover {
  background: #667eea;
  color: white;
}

/* Switch Toggle */
.switch {
  position: relative;
  display: inline-block;
  width: 50px;
  height: 24px;
}

.switch input {
  opacity: 0;
  width: 0;
  height: 0;
}

.slider {
  position: absolute;
  cursor: pointer;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: #ccc;
  transition: .4s;
  border-radius: 24px;
}

.slider:before {
  position: absolute;
  content: "";
  height: 18px;
  width: 18px;
  left: 3px;
  bottom: 3px;
  background-color: white;
  transition: .4s;
  border-radius: 50%;
}

input:checked + .slider {
  background-color: #667eea;
}

input:checked + .slider:before {
  transform: translateX(26px);
}

.settings-actions {
  display: flex;
  gap: 15px;
  justify-content: flex-end;
  margin-top: 30px;
  padding-top: 20px;
  border-top: 1px solid #eee;
}

.btn {
  padding: 12px 24px;
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

.btn-primary:hover {
  background: #5a6fd8;
}

.btn-secondary {
  background: #6c757d;
  color: white;
}

.btn-secondary:hover {
  background: #5a6268;
}

/* Certificate Modal */
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

.certificate-modal {
  background: white;
  border-radius: 15px;
  max-width: 600px;
  width: 100%;
  max-height: 90vh;
  overflow-y: auto;
}

.certificate-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  border-bottom: 1px solid #eee;
}

.certificate-header h2 {
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

.certificate-content {
  padding: 20px;
}

.certificate-design {
  margin-bottom: 30px;
}

.cert-border {
  border: 8px solid #667eea;
  border-radius: 15px;
  padding: 40px;
  text-align: center;
  background: linear-gradient(135deg, #f8f9fa, #ffffff);
  position: relative;
}

.cert-title {
  font-size: 2.5rem;
  font-weight: 700;
  color: #667eea;
  margin-bottom: 10px;
}

.cert-subtitle {
  font-size: 1.2rem;
  color: #666;
  margin-bottom: 30px;
  font-style: italic;
}

.cert-body {
  margin-bottom: 30px;
}

.cert-text {
  font-size: 1.1rem;
  color: #333;
  margin-bottom: 10px;
}

.cert-name {
  font-size: 2rem;
  font-weight: 700;
  color: #667eea;
  margin: 20px 0;
}

.cert-activity {
  font-size: 1.5rem;
  font-weight: 600;
  color: #333;
  margin: 20px 0;
}

.cert-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.cert-date {
  font-size: 1rem;
  color: #666;
}

.cert-seal {
  font-size: 3rem;
}

.certificate-actions {
  display: flex;
  gap: 15px;
  justify-content: center;
}

/* Responsive */
@media (max-width: 768px) {
  .profile-content {
    flex-direction: column;
    text-align: center;
  }
  
  .avatar-section {
    flex-direction: column;
    gap: 20px;
  }
  
  .user-stats {
    justify-content: center;
  }
  
  .level-section {
    min-width: auto;
    width: 100%;
  }
  
  .tabs-container {
    overflow-x: auto;
    padding-bottom: 10px;
  }
  
  .tab-btn {
    flex-shrink: 0;
    padding: 15px 20px;
  }
  
  .activity-record {
    flex-direction: column;
    gap: 15px;
  }
  
  .activity-date {
    align-self: flex-start;
  }
  
  .activity-details {
    flex-direction: column;
    gap: 10px;
  }
  
  .tree-visualization {
    padding: 10px;
  }
  
  .tree-trunk {
    gap: 30px;
  }
  
  .trunk-line {
    display: none;
  }
  
  .milestone {
    flex-direction: column;
    gap: 15px;
    text-align: center;
  }
  
  .milestone:nth-child(even) {
    flex-direction: column;
  }
  
  .milestone:nth-child(even) .milestone-content {
    text-align: center;
  }
  
  .milestone-icon {
    width: 70px;
    height: 70px;
    font-size: 1.8rem;
  }
  
  .milestone-content {
    text-align: center;
    max-width: 100%;
    margin: 0 auto;
  }
  
  .milestone-title {
    font-size: 1.2rem;
  }
  
  .milestone-description {
    font-size: 0.95rem;
  }
  
  .milestone-requirement {
    font-size: 0.85rem;
  }
  
  .points-summary {
    grid-template-columns: 1fr;
  }
  
  .profile-cards {
    grid-template-columns: 1fr;
    gap: 20px;
  }
  
  .stats-grid {
    grid-template-columns: 1fr;
    gap: 12px;
  }
  
  .info-row {
    flex-direction: column;
    align-items: stretch;
    gap: 8px;
  }
  
  .info-value {
    text-align: left;
    margin-left: 0;
  }
  
  .card-header {
    flex-direction: column;
    gap: 15px;
    align-items: stretch;
  }
  
  .edit-profile-btn,
  .view-all-btn {
    align-self: flex-end;
  }
  
  .setting-item {
    flex-direction: column;
    align-items: stretch;
    gap: 10px;
  }
  
  .setting-control {
    justify-content: flex-end;
  }
  
  .setting-input,
  .setting-textarea {
    width: 100%;
  }
  
  .certificate-actions {
    flex-direction: column;
  }
}
</style>