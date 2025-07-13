import axios from 'axios';

const instance = axios.create({
  baseURL: 'http://localhost:8081/api',
  timeout: 1000,
  headers: {'Content-Type': 'application/json'}
});

export const getUsers = () => instance.get('/users');

export default instance;