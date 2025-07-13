<template>
  <div class="admin-login-container">
    <div class="admin-login-card">
      <div class="admin-header">
        <h2>管理员登录</h2>
        <p>请使用管理员账号登录系统</p>
      </div>
      
      <form @submit.prevent="handleLogin" class="admin-login-form">
        <div v-if="error" class="error-message">{{ error }}</div>
        <div class="form-group">
          <label for="adminEmail">管理员邮箱</label>
          <input
            type="email"
            id="adminEmail"
            v-model="loginForm.email"
            required
            placeholder="请输入管理员邮箱"
          />
        </div>
        
        <div class="form-group">
          <label for="adminPassword">密码</label>
          <input
            type="password"
            id="adminPassword"
            v-model="loginForm.password"
            required
            placeholder="请输入密码"
          />
        </div>
        
        
        
        <button type="submit" class="admin-login-btn" :disabled="isLoading">
          {{ isLoading ? '登录中...' : '管理员登录' }}
        </button>
      </form>
      
      <div class="admin-footer">
        <router-link to="/login" class="back-to-user-login">
          返回用户登录
        </router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'

const router = useRouter()
const isLoading = ref(false)

const loginForm = ref({
  email: '',
  password: ''
})

const handleLogin = () => {
  router.push('/admin/dashboard')
}
</script>

<style scoped>
.admin-login-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

.admin-login-card {
  background: white;
  border-radius: 12px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
  padding: 40px;
  width: 100%;
  max-width: 450px;
  border: 2px solid #ff6b6b;
}

.admin-header {
  text-align: center;
  margin-bottom: 30px;
}

.admin-header h2 {
  color: #333;
  margin-bottom: 8px;
  font-size: 28px;
  font-weight: 600;
}

.admin-header p {
  color: #666;
  font-size: 14px;
}

.admin-login-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.form-group {
  display: flex;
  flex-direction: column;
}

.form-group label {
  margin-bottom: 8px;
  color: #333;
  font-weight: 500;
  font-size: 14px;
}

.form-group input {
  padding: 12px 16px;
  border: 2px solid #e1e5e9;
  border-radius: 8px;
  font-size: 16px;
  transition: border-color 0.3s ease;
}

.form-group input:focus {
  outline: none;
  border-color: #ff6b6b;
  box-shadow: 0 0 0 3px rgba(255, 107, 107, 0.1);
}

.admin-login-btn {
  background: linear-gradient(135deg, #ff6b6b, #ee5a52);
  color: white;
  border: none;
  padding: 14px 24px;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  margin-top: 10px;
}

.admin-login-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(255, 107, 107, 0.3);
}

.admin-login-btn:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.admin-footer {
  text-align: center;
  margin-top: 20px;
}

.back-to-user-login {
  color: #666;
  text-decoration: none;
  font-size: 14px;
  transition: color 0.3s ease;
}

.back-to-user-login:hover {
  color: #ff6b6b;
}

@media (max-width: 480px) {
  .admin-login-card {
    padding: 30px 20px;
  }
  
  .admin-header h2 {
    font-size: 24px;
  }
}
</style>