<script setup>
import { useRouter } from 'vue-router'
import { logOut } from '@/api/user'
import { ElMessage } from 'element-plus'
import { onMounted } from 'vue'

const currentUser = JSON.parse(localStorage.getItem('loginUser'))
const router = useRouter()

const logout = async () => {
  localStorage.removeItem('loginUser')
  router.push('/login') // 跳转到登录页
  const res = await logOut()
  if (res.code) {
    ElMessage.success('退出成功')
    
  } else {
    ElMessage.error('退出失败')
  }
}




</script>

<template>
  <el-container style="height: 100vh; width: 100%;">
    <el-header style="background-color: #409EFF; color: white; font-size: 18px; display: flex; align-items: center; justify-content: space-between;">
      <div>欢迎，{{ currentUser.username }}</div>
      <el-button type="text" @click="logout" style="color: white;">退出登录</el-button>
    </el-header>
    <el-container>
      <el-aside>
        <!-- 侧边工具栏内容 -->
        <div class="tool-title">工具栏</div>
        <el-menu :default-active="$route.path" router>
          <el-menu-item index="/home/index">个人首页</el-menu-item>
          <el-menu-item index="/home/blogs">博客空间</el-menu-item>
          <el-menu-item index="/home/chatRoom">聊天室</el-menu-item>
          <el-menu-item index="/home/settings">设置</el-menu-item>
        </el-menu>
      </el-aside>
      <el-main style="padding: 20px; position: relative; ">
        <router-view></router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<style scoped>
.el-aside {
  background-color: #f5f7fa;
  width: 220px;
  border-right: 1px solid #ddd;
  padding: 20px;
  box-shadow: 2px 0 6px rgba(0,0,0,0.1);
  border-radius: 8px;
  margin: 20px;
}

.tool-title {
  font-size: 16px;
  font-weight: bold;
  margin-bottom: 15px;
  color: #333;
}

.el-menu {
  border-right: none;
  background-color: transparent;
}

.el-menu-item {
  border-radius: 6px;
  margin: 6px 0;
  transition: background-color 0.3s, transform 0.3s ease;
  background-color: #ffffff;
}

.el-menu-item:hover {
  transform: scale(1.05);
}
</style>