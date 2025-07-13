<template>
  <div class="static-login-container">
    <div class="login-card">
      <div class="header">
        <h1>志愿者平台</h1>
        <p class="subtitle">临时访问页面</p>
      </div>
      
      <div class="content">
        <div class="welcome-message">
          <h2>欢迎使用志愿者平台</h2>
          <p>由于登录功能暂时维护中，您可以直接访问个人中心体验平台功能。</p>
        </div>
        
        <div class="action-buttons">
          <button @click="goToProfile" class="primary-btn">
            <i class="icon">👤</i>
            进入个人中心
          </button>
          
          <button @click="goToHome" class="secondary-btn">
            <i class="icon">🏠</i>
            返回首页
          </button>
          
          <button @click="goToActivities" class="secondary-btn">
            <i class="icon">📅</i>
            查看活动
          </button>
        </div>
        
        <div class="info-section">
          <h3>平台功能</h3>
          <div class="feature-list">
            <div class="feature-item">
              <span class="feature-icon">✨</span>
              <span>个人资料管理</span>
            </div>
            <div class="feature-item">
              <span class="feature-icon">🏆</span>
              <span>活动记录查看</span>
            </div>
            <div class="feature-item">
              <span class="feature-icon">🌱</span>
              <span>成长树展示</span>
            </div>
            <div class="feature-item">
              <span class="feature-icon">💎</span>
              <span>积分系统</span>
            </div>
          </div>
        </div>
      </div>
      
      <div class="footer">
        <p>登录功能修复后，您可以正常使用完整功能</p>
      </div>
    </div>
  </div>
</template>

<script>
import { useRouter } from 'vue-router'

export default {
  name: 'StaticLoginPage',
  setup() {
    const router = useRouter()
    
    const goToProfile = () => {
      router.push('/profile')
    }
    
    const goToHome = () => {
      router.push('/')
    }
    
    const goToActivities = () => {
      router.push('/activities')
    }
    
    return {
      goToProfile,
      goToHome,
      goToActivities
    }
  }
}
</script>

<style scoped>
.static-login-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
  font-family: 'Arial', sans-serif;
}

.login-card {
  background: white;
  border-radius: 20px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
  max-width: 500px;
  width: 100%;
  overflow: hidden;
  animation: slideUp 0.6s ease-out;
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 30px;
  text-align: center;
}

.header h1 {
  margin: 0;
  font-size: 2.2rem;
  font-weight: bold;
}

.subtitle {
  margin: 10px 0 0 0;
  opacity: 0.9;
  font-size: 1rem;
}

.content {
  padding: 30px;
}

.welcome-message {
  text-align: center;
  margin-bottom: 30px;
}

.welcome-message h2 {
  color: #333;
  margin: 0 0 15px 0;
  font-size: 1.5rem;
}

.welcome-message p {
  color: #666;
  line-height: 1.6;
  margin: 0;
}

.action-buttons {
  display: flex;
  flex-direction: column;
  gap: 15px;
  margin-bottom: 30px;
}

.primary-btn, .secondary-btn {
  padding: 15px 20px;
  border: none;
  border-radius: 10px;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
}

.primary-btn {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.primary-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 10px 20px rgba(102, 126, 234, 0.3);
}

.secondary-btn {
  background: #f8f9fa;
  color: #333;
  border: 2px solid #e9ecef;
}

.secondary-btn:hover {
  background: #e9ecef;
  transform: translateY(-1px);
}

.icon {
  font-size: 1.2rem;
}

.info-section {
  border-top: 1px solid #e9ecef;
  padding-top: 20px;
}

.info-section h3 {
  color: #333;
  margin: 0 0 15px 0;
  font-size: 1.2rem;
  text-align: center;
}

.feature-list {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 10px;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px;
  background: #f8f9fa;
  border-radius: 8px;
  font-size: 0.9rem;
  color: #555;
}

.feature-icon {
  font-size: 1rem;
}

.footer {
  background: #f8f9fa;
  padding: 20px;
  text-align: center;
  color: #666;
  font-size: 0.9rem;
  border-top: 1px solid #e9ecef;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .static-login-container {
    padding: 10px;
  }
  
  .login-card {
    margin: 0;
  }
  
  .header {
    padding: 20px;
  }
  
  .header h1 {
    font-size: 1.8rem;
  }
  
  .content {
    padding: 20px;
  }
  
  .feature-list {
    grid-template-columns: 1fr;
  }
}
</style>