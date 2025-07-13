<template>
  <div class="home-page">
    <!-- Hero Section -->
    <section class="hero">
      <div class="hero-content">
        <h1 class="hero-title">让爱心成长，让世界更美好</h1>
        <p class="hero-subtitle">加入我们的志愿者大家庭，用行动传递温暖，用服务点亮希望</p>
        <div class="hero-buttons">
          <button class="btn btn-primary" @click="$emit('tab-change', 'activities')">探索活动</button>
          <button class="btn btn-secondary" @click="$emit('show-register')">立即加入</button>
        </div>
      </div>
      
    </section>

    <!-- Stats Section -->
    <section class="stats">
      <div class="stats-container">
        <div class="stat-item">
          <div class="stat-number">{{ animatedStats.volunteers }}</div>
          <div class="stat-label">注册志愿者</div>
        </div>
        <div class="stat-item">
          <div class="stat-number">{{ animatedStats.activities }}</div>
          <div class="stat-label">举办活动</div>
        </div>
        <div class="stat-item">
          <div class="stat-number">{{ animatedStats.hours }}</div>
          <div class="stat-label">服务时长</div>
        </div>
        <div class="stat-item">
          <div class="stat-number">{{ animatedStats.beneficiaries }}</div>
          <div class="stat-label">受益人数</div>
        </div>
      </div>
    </section>

    <!-- Features Section -->
    <section class="features">
      <div class="features-container">
        <h2 class="section-title">平台特色</h2>
        <div class="features-grid">
          <div class="feature-card" v-for="feature in features" :key="feature.id">
            <div class="feature-icon">{{ feature.icon }}</div>
            <h3 class="feature-title">{{ feature.title }}</h3>
            <p class="feature-description">{{ feature.description }}</p>
          </div>
        </div>
      </div>
    </section>

    <!-- Recent Activities -->
    <section class="recent-activities">
      <div class="activities-container">
        <h2 class="section-title">最新活动</h2>
        <div class="activities-grid">
          <div class="activity-card" v-for="activity in recentActivities" :key="activity.id">
            <div class="activity-image">
              <img :src="activity.image" :alt="activity.title">
              <div class="activity-status">{{ activity.status }}</div>
            </div>
            <div class="activity-content">
              <h3 class="activity-title">{{ activity.title }}</h3>
              <p class="activity-description">{{ activity.description }}</p>
              <div class="activity-meta">
                <span class="activity-date">📅 {{ activity.date }}</span>
                <span class="activity-location">📍 {{ activity.location }}</span>
              </div>
              <div class="activity-footer">
                <span class="activity-participants">👥 {{ activity.participants }}/{{ activity.maxParticipants }}</span>
                <button class="btn btn-small btn-primary">立即报名</button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, onMounted, defineEmits } from 'vue'

const emit = defineEmits(['tab-change', 'show-register'])

// 动画统计数据
const animatedStats = ref({
  volunteers: 0,
  activities: 0,
  hours: 0,
  beneficiaries: 0
})

const targetStats = {
  volunteers: 1250,
  activities: 368,
  hours: 15680,
  beneficiaries: 8900
}

// 平台特色
const features = ref([
  {
    id: 1,
    icon: '🌱',
    title: '成长激励',
    description: '通过志愿服务获得积分，见证自己的成长轨迹'
  },
  {
    id: 2,
    icon: '🎯',
    title: '精准匹配',
    description: '智能推荐适合的志愿活动，让每份爱心都有归属'
  },
  {
    id: 3,
    icon: '💬',
    title: '社区分享',
    description: '分享志愿故事，传递正能量，建立温暖社区'
  },
  {
    id: 4,
    icon: '🏆',
    title: '荣誉认证',
    description: '获得官方认证，让志愿服务成为人生亮点'
  }
])

// 最新活动
const recentActivities = ref([
  {
    id: 1,
    title: '关爱空巢老人',
    description: '陪伴社区独居老人，为他们带去温暖和关怀',
    image: 'https://plus.unsplash.com/premium_photo-1663100162202-c1e6fa87bc5a?q=80&w=687&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D',
    date: '2024-01-15',
    location: '阳光社区',
    participants: 12,
    maxParticipants: 20,
    status: '报名中'
  },
  {
    id: 2,
    title: '环保清洁行动',
    description: '保护环境从我做起，共建美丽家园',
    image: 'https://plus.unsplash.com/premium_photo-1664372899236-9d0654e2507f?q=80&w=695&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D',
    date: '2024-01-18',
    location: '滨江公园',
    participants: 25,
    maxParticipants: 30,
    status: '报名中'
  },
  {
    id: 3,
    title: '儿童阅读推广',
    description: '为山区儿童带去知识的力量和阅读的快乐',
    image: 'https://plus.unsplash.com/premium_photo-1664105110798-0530b3e0d153?q=80&w=687&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D',
    date: '2024-01-20',
    location: '希望小学',
    participants: 8,
    maxParticipants: 15,
    status: '报名中'
  }
])

// 成长树样式
const getLeafStyle = (index) => {
  const angles = [0, 45, 90, 135, 180, 225, 270, 315]
  const radius = 60 + (index % 3) * 20
  const angle = angles[index - 1] * Math.PI / 180
  return {
    transform: `translate(${Math.cos(angle) * radius}px, ${Math.sin(angle) * radius}px)`,
    animationDelay: `${index * 0.2}s`
  }
}

const getFruitStyle = (index) => {
  const angles = [30, 90, 150, 210, 330]
  const radius = 40
  const angle = angles[index - 1] * Math.PI / 180
  return {
    transform: `translate(${Math.cos(angle) * radius}px, ${Math.sin(angle) * radius}px)`,
    animationDelay: `${index * 0.3}s`
  }
}

// 数字动画
const animateNumber = (key, target, duration = 2000) => {
  const start = 0
  const increment = target / (duration / 16)
  let current = start
  
  const timer = setInterval(() => {
    current += increment
    if (current >= target) {
      current = target
      clearInterval(timer)
    }
    animatedStats.value[key] = Math.floor(current)
  }, 16)
}

onMounted(() => {
  // 启动数字动画
  setTimeout(() => {
    Object.keys(targetStats).forEach((key, index) => {
      setTimeout(() => {
        animateNumber(key, targetStats[key])
      }, index * 200)
    })
  }, 500)
})
</script>

<style scoped>
.home-page {
  min-height: 100vh;
}

/* Hero Section */
.hero {
  display: flex;
  align-items: center;
  justify-content: center;
  text-align: center;
  padding: 100px 20px;
  background: linear-gradient(135deg, #89f7fe 0%, #66a6ff 100%);
  color: white;
  min-height: 50vh;
}

.hero-content {
  flex: 1;
  max-width: 600px;
  margin: 0 auto;
}

.hero-title {
  font-size: 3rem;
  font-weight: 700;
  margin-bottom: 20px;
  line-height: 1.2;
}

.hero-subtitle {
  font-size: 1.2rem;
  margin-bottom: 40px;
  opacity: 0.9;
  line-height: 1.6;
}

.hero-buttons {
  display: flex;
  gap: 20px;
  flex-wrap: wrap;
}

.hero-image {
  flex: 1;
  display: flex;
  justify-content: center;
  align-items: center;
}

.growth-tree {
  position: relative;
  width: 200px;
  height: 200px;
}

.tree-trunk {
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 20px;
  height: 80px;
  background: #8B4513;
  border-radius: 10px;
}

.tree-leaves,
.tree-fruits {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
}

.leaf,
.fruit {
  position: absolute;
  font-size: 1.5rem;
  animation: float 3s ease-in-out infinite;
}

.fruit {
  animation: pulse 2s ease-in-out infinite;
}

@keyframes float {
  0%, 100% { transform: translateY(0px); }
  50% { transform: translateY(-10px); }
}

@keyframes pulse {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.1); }
}

/* Stats Section */
.stats {
  background: #f8f9fa;
  padding: 60px 20px;
}

.stats-container {
  max-width: 1200px;
  margin: 0 auto;
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 40px;
}

.stat-item {
  text-align: center;
  background: white;
  padding: 30px;
  border-radius: 15px;
  box-shadow: 0 5px 15px rgba(0,0,0,0.08);
  transition: transform 0.3s ease;
}

.stat-item:hover {
  transform: translateY(-5px);
}

.stat-number {
  font-size: 2.5rem;
  font-weight: 700;
  color: #667eea;
  margin-bottom: 10px;
}

.stat-label {
  color: #666;
  font-weight: 500;
}

/* Features Section */
.features {
  padding: 80px 20px;
}

.features-container {
  max-width: 1200px;
  margin: 0 auto;
}

.section-title {
  text-align: center;
  font-size: 2.5rem;
  margin-bottom: 60px;
  color: #333;
}

.features-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 30px;
}

.feature-card {
  text-align: center;
  padding: 40px 20px;
  border-radius: 15px;
  background: white;
  box-shadow: 0 5px 15px rgba(0,0,0,0.08);
  transition: transform 0.3s ease;
}

.feature-card:hover {
  transform: translateY(-10px);
}

.feature-icon {
  font-size: 3rem;
  margin-bottom: 20px;
}

.feature-title {
  font-size: 1.3rem;
  margin-bottom: 15px;
  color: #333;
}

.feature-description {
  color: #666;
  line-height: 1.6;
}

/* Recent Activities */
.recent-activities {
  background: #f8f9fa;
  padding: 80px 20px;
}

.activities-container {
  max-width: 1200px;
  margin: 0 auto;
}

.activities-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 30px;
}

.activity-card {
  background: white;
  border-radius: 15px;
  overflow: hidden;
  box-shadow: 0 5px 15px rgba(0,0,0,0.08);
  transition: transform 0.3s ease;
}

.activity-card:hover {
  transform: translateY(-5px);
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
}

.activity-status {
  position: absolute;
  top: 10px;
  right: 10px;
  background: #4CAF50;
  color: white;
  padding: 5px 10px;
  border-radius: 15px;
  font-size: 0.8rem;
}

.activity-content {
  padding: 20px;
}

.activity-title {
  font-size: 1.2rem;
  margin-bottom: 10px;
  color: #333;
}

.activity-description {
  color: #666;
  margin-bottom: 15px;
  line-height: 1.5;
}

.activity-meta {
  display: flex;
  gap: 15px;
  margin-bottom: 15px;
  font-size: 0.9rem;
  color: #888;
}

.activity-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.activity-participants {
  color: #666;
  font-size: 0.9rem;
}

/* Buttons */
.btn {
  padding: 12px 30px;
  border: none;
  border-radius: 25px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  text-decoration: none;
  display: inline-block;
  font-size: 1rem;
}

.btn-primary {
  background: #ff6b6b;
  color: white;
}

.btn-primary:hover {
  background: #ff5252;
  transform: translateY(-2px);
}

.btn-secondary {
  background: transparent;
  color: white;
  border: 2px solid white;
}

.btn-secondary:hover {
  background: white;
  color: #667eea;
}

.btn-small {
  padding: 8px 20px;
  font-size: 0.9rem;
}

/* Responsive */
@media (max-width: 768px) {
  .hero {
    flex-direction: column;
    text-align: center;
    padding: 60px 20px;
  }
  
  .hero-title {
    font-size: 2rem;
  }
  
  .hero-buttons {
    justify-content: center;
  }
  
  .section-title {
    font-size: 2rem;
  }
  
  .stats-container {
    grid-template-columns: repeat(2, 1fr);
    gap: 20px;
  }
}
</style>