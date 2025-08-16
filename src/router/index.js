import { createRouter, createWebHistory } from 'vue-router'
import RegisterView from '@/views/register/register.vue'
import LoginView from '@/views/login/login.vue'
import HomeView from '@/views/home/home.vue'
// 子路由组件
import BlogView from '@/views/blogs/blogs.vue'
import SettingsView from '@/views/settings/settings.vue'
import IndexView from '@/views/home/index.vue';
import chatRoom from '@/ChatRoom/chatRoom.vue'
const routes = [
    {
    path: '/',
    redirect: '/login' // 默认跳转到登录页
  },
   {
    path: '/index',
    redirect: '/home/index'
  },
  {
    path: '/home',
    name: 'Home',
    component: HomeView,
    redirect: '/home/index',
    children: [
      {
        path: 'index',
        name: 'Index',
        component: IndexView
      },
      {
        path: 'blogs',
        name: 'Blogs',
        component: BlogView
      },
      {
        path: '/blogs/:id/:username',
        name: 'BlogDetail',
        component: () => import('@/views/blogs/blogDetail.vue')
      },
      {
        path: 'settings',
        name: 'Settings',
        component: SettingsView
      },
      {
        path: 'chatRoom',
        name: 'chatRoom',
        component: chatRoom
      }

    ]
  },
  {
    path: '/login',
    name: 'Login',
    component: LoginView
  },
  {
    path: '/register',
    name: 'Register',
    component: RegisterView
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router