<script setup>
import { ref } from 'vue'
import { registerUser } from '@/api/user'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'

const router = useRouter()

const form = ref({
  username: '',
  password: '',
  image: '',
  selfIntro: ''
})

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' }
  ]
}

//文件上传之前触发
const beforeAvatarUpload = (file) => { 
  if (file.type !== 'image/jpeg' && file.type !== 'image/png') { 
    ElMessage.error('只支持上传图片');
    return false;
  }
  if (file.size > 1024 * 1024*10) { 
    ElMessage.error('图片大小不能超过10M');
    return false;
  }
  return true;
};

const handleAvatarSuccess= (response)=>{
  ElMessage.success('上传成功');
  form.value.image = response.data;
}

const formRef = ref()

const onSubmit = async () => {
  // 触发表单校验
  formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        await registerUser(form.value)
        ElMessage.success('注册成功')
        router.push('/login')
      } catch (error) {
        ElMessage.error('注册失败，请重试')
      }
    } else {
      ElMessage.error('请填写必填项')
      return false
    }
  })
}
</script>

<template>
  <div class="register-container">
    <!-- 添加 ref 属性用于获取表单实例 -->
    <el-form 
      ref="formRef"
      :model="form" 
      :rules="rules" 
      label-width="120px" 
      @submit.prevent="onSubmit" 
      class="register-form">
      <h2 style="text-align: center; margin: 20px 0;">用户注册</h2>
      <div class="form-wrapper">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" style="width: 80%;" placeholder="请输入用户名"/>
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" show-password style="width: 80%;" placeholder="请输入密码"/>
        </el-form-item>
        <el-form-item label="头像" prop="image">
           <el-upload
                class="avatar-uploader"
                action="/api/user/upload"
                :show-file-list="false"
                :on-success="handleAvatarSuccess"
                :before-upload="beforeAvatarUpload">
    <img v-if="form.image" :src="form.image" class="avatar" />
    <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
  </el-upload>
        </el-form-item>
        <el-form-item label="自我介绍">
          <el-input v-model="form.selfIntro" type="textarea" />
        </el-form-item>
        <el-form-item class="button-group">
          <el-button type="primary" native-type="submit">注册</el-button>
          <el-button @click="$router.push('/login')">返回登录</el-button>
        </el-form-item>
      </div>
    </el-form>
  </div>
</template>

<style scoped>
.register-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-color: #f5f7fa; /* 添加背景色 */
}

.register-form {
  max-width: 600px;
  width: 100%;
  padding: 20px;
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1); /* 添加阴影效果 */
}

.form-wrapper {
  padding: 20px 0;
}

.button-group {
  display: flex;
  justify-content: center;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .register-form {
    margin: 20px;
  }
}

.avatar-uploader .avatar {
  width: 178px;
  height: 178px;
  display: block;
}
</style>
<style>
.avatar-uploader .el-upload {
  border: 1px dashed var(--el-border-color);
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: var(--el-transition-duration-fast);
}

.avatar-uploader .el-upload:hover {
  border-color: var(--el-color-primary);
}

.el-icon.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 178px;
  height: 178px;
  text-align: center;
}
</style>