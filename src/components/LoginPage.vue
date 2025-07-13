<template>
  <div class="auth-page">
    <div class="auth-container">
      <div class="auth-card">
        <h2 class="auth-title">欢迎回来</h2>
        <p class="auth-subtitle">登录以继续您的志愿之旅</p>
        <form @submit.prevent="handleLogin">
          <div v-if="error" class="error-message">{{ error }}</div>
          <div class="input-group">
            <label for="email">邮箱地址</label>
            <input type="email" id="email" v-model="email" placeholder="请输入您的邮箱" required>
          </div>
          <div class="input-group">
            <label for="password">密码</label>
            <input type="password" id="password" v-model="password" placeholder="请输入您的密码" required>
          </div>
          <div class="form-actions">
            <a href="#" class="forgot-password">忘记密码?</a>
          </div>
          <button type="submit" class="btn btn-primary btn-block">登录</button>
        </form>
        <div class="switch-auth">
          <p>还没有账户? <router-link to="/register">立即注册</router-link></p>
          <p>管理员登录? <router-link to="/admin/login">管理员登录</router-link></p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useAuth } from '@/composables/useAuth.jsx';
import { useRouter } from 'vue-router';

const email = ref('');
const password = ref('');

const { error, login } = useAuth();
const router = useRouter();

const handleLogin = () => {
  router.push('/profile');
};
</script>

<style scoped>
@import '@/assets/styles/auth.css';

.form-actions {
  display: flex;
  justify-content: flex-end;
  margin-bottom: 20px;
}

.forgot-password {
  color: #667eea;
  text-decoration: none;
  font-size: 0.9rem;
}
</style>