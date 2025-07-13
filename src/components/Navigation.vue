<template>
  <header class="app-header">
    <div class="header-container">
      <div class="logo">
        <h2>🌱 成长树志愿平台</h2>
      </div>
      <nav class="main-nav">
        <router-link to="/" class="nav-link">首页</router-link>
        <router-link to="/activities" class="nav-link">活动广场</router-link>
        <router-link to="/store" class="nav-link">积分商城</router-link>
        <router-link to="/community" class="nav-link">社区故事</router-link>
        <router-link to="/profile" class="nav-link">个人中心</router-link>
      </nav>
      <div class="auth-buttons">
        <template v-if="!user">
          <router-link to="/login" class="btn btn-login">登录</router-link>
          <router-link to="/register" class="btn btn-register">注册</router-link>
        </template>
        <template v-else>
          <router-link v-if="user.role === 'admin'" to="/admin" class="btn btn-admin">管理后台</router-link>
          <a href="#" @click.prevent="logout" class="btn btn-register">退出</a>
        </template>
      </div>
    </div>
  </header>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue';
import { useRouter, useRoute } from 'vue-router';

const router = useRouter();
const route = useRoute();
const user = ref(null);

const updateUser = () => {
  const storedUser = localStorage.getItem('user');
  user.value = storedUser ? JSON.parse(storedUser) : null;
};

onMounted(() => {
  updateUser();
});

watch(() => route.path, () => {
  updateUser();
});

const logout = () => {
  localStorage.removeItem('user');
  user.value = null;
  router.push('/login');
};
</script>

<style scoped>
.app-header {
  background: white;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 1000;
  height: 70px;
}

.header-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 100%;
}

.logo h2 {
  color: #333;
  margin: 0;
  font-size: 1.5rem;
  font-weight: 600;
}

.main-nav {
  display: flex;
  gap: 30px;
}

.nav-link {
  color: #555;
  text-decoration: none;
  font-weight: 500;
  padding: 10px 15px;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.nav-link:hover,
.nav-link.router-link-exact-active {
  background-color: #f0f2f5;
  color: #667eea;
}

.auth-buttons {
  display: flex;
  gap: 10px;
}

.btn-login {
  background: transparent;
  color: #667eea;
  border: 1px solid #667eea;
}

.btn-login:hover {
  background: #667eea;
  color: white;
}

.btn-register {
  background: #667eea;
  color: white;
  border: 1px solid #667eea;
}

.btn-register:hover {
  background: #5a67d8;
}

.btn-admin {
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: white;
  text-decoration: none;
  padding: 10px 20px;
  border-radius: 6px;
  font-weight: 500;
  transition: all 0.3s ease;
  border: 2px solid transparent;
}

.btn-admin:hover {
  background: linear-gradient(135deg, #5a6fd8, #6a42a0);
  transform: translateY(-2px);
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.3);
}
</style>