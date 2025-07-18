import './assets/main.css'

import { createApp } from 'vue'
import App from './App.vue'
import router from './router';
import axios from './api/axios';

const app = createApp(App);

app.config.globalProperties.$axios = axios;

app.use(router).mount('#app');
