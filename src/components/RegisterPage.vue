<template>
  <div class="auth-page">
    <div class="auth-container">
      <div class="auth-card">
        <h2 class="auth-title">加入我们</h2>
        <p class="auth-subtitle">创建账户，开启您的志愿服务之旅</p>
        <form @submit.prevent="handleRegister">
          <div v-if="error" class="error-message">{{ error }}</div>
          <div v-if="success" class="success-message">{{ success }}</div>
          <div class="input-group">
            <label for="username">用户名</label>
            <input type="text" id="username" v-model="username" placeholder="请输入您的用户名" required>
          </div>
          <div class="input-group">
            <label for="email">邮箱地址</label>
            <input type="email" id="email" v-model="email" placeholder="请输入您的邮箱" required>
          </div>
          <div class="input-group">
            <label for="password">密码</label>
            <input type="password" id="password" v-model="password" placeholder="请输入您的密码" required>
          </div>
          <div class="input-group">
            <label for="confirm-password">确认密码</label>
            <input type="password" id="confirm-password" v-model="confirmPassword" placeholder="请再次输入密码" required>
          </div>
          <button type="submit" class="btn btn-primary btn-block">注册</button>
        </form>
        <div class="auth-footer">
          <p>已有账户？ <router-link to="/login">立即登录</router-link></p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useAuth } from '@/composables/useAuth.jsx';

const username = ref('');
const email = ref('');
const password = ref('');
const confirmPassword = ref('');

const { error, success, register: authRegister } = useAuth();

const handleRegister = async () => {
  if (password.value !== confirmPassword.value) {
    error.value = '两次输入的密码不一致';
    return;
  }
  await authRegister(username.value, email.value, password.value);
};
</script>

<style scoped>
@import '@/assets/styles/auth.css';
</style>