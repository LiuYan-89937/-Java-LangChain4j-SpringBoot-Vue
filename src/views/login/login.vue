<script setup>
import { ref,onMounted } from 'vue'
import { loginUser } from '@/api/user'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import { getFollowing } from '@/api/follow'
const router = useRouter()
const form = ref({
  username: '',
  password: ''
})
const resetForm = () => {
  form.value = {
    username: '',
    password: ''
  }
}

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' }
  ]
}

const onSubmit = async () => {
  if(JSON.parse(localStorage.getItem('loginUser'))) {
    ElMessage.error('请勿重复登录')
    return
  }
  
    const res=await loginUser(form.value.username, form.value.password)
    if (res.code) {

      const list = await getFollowing(res.data.id)
      localStorage.setItem('loginUser', JSON.stringify(res.data))
      localStorage.setItem('followingList', JSON.stringify(list.data))
      // 可以在这里添加跳转逻辑
      router.push('/home')
      ElMessage.success('登录成功')
      // 保存用户信息到本地存储
      
    } else {
      ElMessage.error('登录失败，请检查用户名和密码')
    }

}
onMounted(()=>{
   const currentUser = localStorage.getItem('loginUser')
  if (currentUser) {
    // 如果有当前用户，直接跳转到主页
    router.push('/home')
    return
  }

  resetForm()
})
</script>

<template>
  <div class="login-container">
    <el-form :model="form" :rules="rules" label-width="120px" @submit.prevent="onSubmit" class="login-form">
      <h2 style="text-align: center; margin-bottom: 30px; color: #333;">用户登录</h2>
      <el-form-item label="用户名" prop="username">
        <el-input v-model="form.username" placeholder="请输入用户名" style="width: 70%;" />
      </el-form-item>
      <el-form-item label="密码" prop="password">
        <el-input v-model="form.password" type="password" show-password placeholder="请输入密码" style="width: 70%;" />
      </el-form-item>
      <el-form-item style="margin-left: 50px;">
        <el-button type="primary" native-type="submit" style="width: 50%; border-radius: 8px; padding: 12px 0;">登录</el-button>
      </el-form-item>
      <el-form-item style="margin-left: 55px;">
        <el-button type="text" style="width: 50%; text-align: center; color: #409EFF; font-weight: bold;">
          <router-link to="/register" style="text-decoration: none; color: inherit;">没有账号？立即注册</router-link>
        </el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background: linear-gradient(135deg, #f0f4f8, #d9e2ec);
}

.login-form {
  max-width: 500px;
  width: 100%;
  padding: 30px;
  background-color: #fff;
  border-radius: 12px;
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.1);
}
</style>