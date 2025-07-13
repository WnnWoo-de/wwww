import { createRouter, createWebHistory } from 'vue-router'
const routes = [
  {
    path: '/',
    name: 'Home',
    component: () => import('../components/HomePage.vue')
  },
  {
    path: '/activities',
    name: 'Activities',
    component: () => import('../components/ActivitiesPage.vue')
  },
  {
    path: '/store',
    name: 'Store',
    component: () => import('../components/StorePage.vue')
  },
  {
    path: '/community',
    name: 'Community',
    component: () => import('../components/CommunityPage.vue')
  },
  {
    path: '/profile',
    name: 'Profile',
    component: () => import('../components/ProfilePage.vue')
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('../components/LoginPage.vue')
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('../components/RegisterPage.vue')
  },
  {
    path: '/admin/login',
    name: 'AdminLogin',
    component: () => import('../components/AdminLoginPage.vue')
  },
  {
    path: '/admin/dashboard',
    name: 'AdminDashboard',
    component: () => import('../components/AdminDashboard.vue')
  },
  {
    path: '/admin/activities',
    name: 'AdminActivities',
    component: () => import('../components/AdminActivities.vue')
  },
  {
    path: '/admin/volunteers',
    name: 'AdminVolunteers',
    component: () => import('../components/AdminVolunteers.vue')
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const loggedIn = localStorage.getItem('user');
  const user = loggedIn ? JSON.parse(loggedIn) : null;

  const requiresAuth = to.matched.some(record => record.meta.requiresAuth);
  const requiredRole = to.meta.role;

  // If the route requires authentication
  if (requiresAuth) {
    // If the user is not logged in, redirect to the login page
    if (!loggedIn) {
      next('/login');
      return;
    }

    // If the route requires a specific role and the user does not have it,
    // redirect to the home page (or a 'not-authorized' page)
    if (requiredRole && user.role !== requiredRole) {
      next('/');
      return;
    }
  }

  // Otherwise, allow the user to proceed
  next();
});

export default router