import { ref } from 'vue';
import { useRouter } from 'vue-router';
import axios from '../api/axios';

export function useAuth() {
  const user = ref(JSON.parse(localStorage.getItem('user')));
  const error = ref('');
  const success = ref('');
  const router = useRouter();

  const login = async (email, password) => {
    error.value = '';
    try {
      const response = await axios.post('/api/users/login', { email, password });
      if (response.data.token) {
        localStorage.setItem('token', response.data.token);
        user.value = response.data.user;
        localStorage.setItem('user', JSON.stringify(user.value));
        router.push('/profile');
      } else {
        error.value = response.data.message;
      }
    } catch (err) {
      error.value = '登录时发生错误。';
      console.error(err);
    }
  };

  const register = async (username, email, password) => {
    error.value = '';
    success.value = '';
    try {
      const response = await axios.post('/api/users/register', { username, email, password });
      if (response.data.success) {
        success.value = '注册成功！';
        router.push('/login');
      } else {
        error.value = response.data.message;
      }
    } catch (err) {
      if (err.response && err.response.data && err.response.data.message) {
        error.value = err.response.data.message;
      } else {
        error.value = '注册时发生错误。';
      }
      console.error(err);
    }
  };

  const logout = () => {
    user.value = null;
    localStorage.removeItem('user');
    localStorage.removeItem('token');
    router.push('/login');
  };

  const checkAuth = () => {
    const storedUser = localStorage.getItem('user');
    if (storedUser) {
      user.value = JSON.parse(storedUser);
    }
  };

  return {
    user,
    error,
    success,
    login,
    register,
    logout,
    checkAuth,
    getProfile
  };
}

const getProfile = async () => {
  try {
    const token = localStorage.getItem('token');
    if (!token) {
      throw new Error('No token found');
    }
    const response = await axios.get('/api/users/profile', {
      headers: {
        Authorization: `Bearer ${token}`
      }
    });
    return response.data;
  } catch (err) {
    console.error('Failed to fetch profile:', err);
    throw err;
  }
};