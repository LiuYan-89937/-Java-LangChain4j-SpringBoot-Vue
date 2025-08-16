<script setup>
import { ref, onMounted, onBeforeUnmount, shallowRef } from 'vue'
import { getUserInfo, updateUserInfo } from '@/api/user'
import { getBlogs, addBlogs,searchBlogsByKey,deleteBlogById } from '@/api/blog' // 引入 addBlogs
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getFollowing, followUserById, unfollowUserById } from '@/api/follow'
// 引入 wangEditor
import { Editor, Toolbar } from '@wangeditor/editor-for-vue'
import '@wangeditor/editor/dist/css/style.css'

const router = useRouter()
const currentUser = ref({})
const editForm = ref({})
const blogs = ref([])
const dialogVisible = ref(false) // 控制对话框显示的变量
const dialogVisibleAddBlog = ref(false)
// 搜索条件对象
const searchForm = ref({
  title: '',
  content: '',
  username: '',
  startTime: '',
  endTime: ''
})

// 分页信息
const pagination = ref({
  page: 1,
  pageSize: 9,
  total: 0
})
// 新增：搜索与新增相关数据
const newBlog = ref({
  title: '',
  content: ''
})
// 控制搜索表单显示
const showSearchForm = ref(false)
// 文件上传之前触发
const beforeAvatarUpload = (file) => {
  if (file.type !== 'image/jpeg' && file.type !== 'image/png') {
    ElMessage.error('只支持上传图片')
    return false
  }
  if (file.size > 1024 * 1024 * 10) {
    ElMessage.error('图片大小不能超过10M')
    return false
  }
  return true
}

const openEditDialog = () => {
  editForm.value = JSON.parse(JSON.stringify(currentUser.value))
  dialogVisible.value = true
}

// 处理头像上传成功
const handleEditAvatarSuccess = (response) => {
  ElMessage.success('上传成功')
  // 同时更新当前用户和编辑表单中的头像
  currentUser.value.image = response.data
  editForm.value.image = response.data
}

// 保存用户信息
const saveUserInfo = async () => {
    const res = await updateUserInfo(editForm.value)
  if (res.code) {
    currentUser.value = JSON.parse(JSON.stringify(editForm.value))
    ElMessage.success('信息已保存')
  } else {
    ElMessage.error(res.msg)
    return
  }

  dialogVisible.value = false
}

// 编辑器实例，必须用 shallowRef
const editorRef = shallowRef()

// 富文本编辑器配置
const editorConfig = {
  placeholder: '请输入内容...',
  MENU_CONF: {
    uploadImage: {
      server: '/api/user/upload',
      fieldName: 'file',
      maxFileSize: 2 * 1024 * 1024, // 2M
      // 添加 customInsert 方法处理返回数据
      customInsert(res, insertFn) {
        // 假设后端返回格式为 { code: 200, data: { url: '图片地址' } }
        // 根据您的实际后端返回格式调整这部分代码
        if (res.code) {
          const url = res.data // 根据实际返回结构调整
          insertFn(url, 'image.png', url) // 插入图片到编辑器
        } else {
          ElMessage.error('图片上传失败: ' + (res.msg || '未知错误'))
        }
      },
      onError(file, err, res) {
        ElMessage.error('图片上传失败: ' + (err?.message || '未知错误'))
      }
    }
  }
}

// 提交新增博客
const submitBlog = async () => {
  try {
    // 获取编辑器内容
    if (editorRef.value) {
      newBlog.value.content = editorRef.value.getHtml()
    }
    
    if (!newBlog.value.title.trim()) {
      ElMessage.warning('请输入博客标题')
      return
    }
    
    if (!newBlog.value.content || newBlog.value.content === '<p><br></p>') {
      ElMessage.warning('请输入博客内容')
      return
    }
    
    const res = await addBlogs(newBlog.value)
    if (res.code) {
      ElMessage.success('新增成功')
      dialogVisibleAddBlog.value = false
      newBlog.value.title = ''
      newBlog.value.content = ''
      // 重置编辑器内容
      if (editorRef.value) {
        editorRef.value.clear()
      }
      await searchBlogs()
    } else {
      ElMessage.error('新增失败')
    }
  } catch (error) {
    ElMessage.error('新增失败: ' + error.message)
  }
}

// 搜索博客
const searchBlogs = async() => {
 try {
    const params = {
      page: pagination.value.page,
      pageSize: pagination.value.pageSize,
      title: searchForm.value.title,
      content: searchForm.value.content,
      username: JSON.parse(localStorage.getItem('loginUser')).username,
      startTime: searchForm.value.startTime,
      endTime: searchForm.value.endTime
    }
    
    const res = await searchBlogsByKey(params)
    if (res.code) {
      blogs.value = res.data.rows
      pagination.value.total = res.data.total
    } else {
      ElMessage.error('搜索失败')
    }
  } catch (error) {
    ElMessage.error('搜索失败: ' + error.message)
  }
 
}
const resetSearch = () => {
  searchForm.value = {
    title: '',
    content: '',
    username: '',
    startTime: '',
    endTime: ''
  }
  pagination.value.page = 1
  searchBlogs()
}
// 点击卡片的事件处理函数
const handleBlogClick = (blog) => {
  router.push(`/blogs/${blog.id}/${blog.username}`)
}

const getRandomColor = () => {
  const colors = ['#FFDDC1', '#C1F0F6', '#FFE8F0', '#D4EDF9', '#E2F0CB'];
  return colors[Math.floor(Math.random() * colors.length)];
}

// 新增：获取关注列表
const followingList = ref([])
// 新增：为每个关注用户添加isFollowing状态
const followingStatus = ref({})

// 修改：fetchFollowingList 函数
const fetchFollowingList = async () => {
  try {
    const res = await getFollowing(currentUser.value.id)
    if (res.code) {
      //循环拿到对应id的用户信息
      for (let i = 0; i < res.data.length; i++) {
        const user = await getUserInfo(res.data[i])
        followingList.value.push(user.data)
        // 初始化关注状态为true（因为已经在关注列表中）
        followingStatus.value[user.data.id] = true
      }
      
      
    }
  } catch (error) {
    ElMessage.error('获取关注列表失败: ' + error.message)
  }
}

// 新增：处理取消关注逻辑
const handleUnfollowUser = async (userId) => {
  const data = {
    userId: JSON.parse(localStorage.getItem('loginUser')).id,
    followId: userId
  }
  const res = await unfollowUserById(data)
  if (res.code) {
    ElMessage.success('取消关注成功')
    followingStatus.value[userId] = false
    // 从关注列表中移除已取消关注的用户
    const index = followingList.value.findIndex(user => user.id === userId)
    if (index !== -1) {
      followingList.value.splice(index, 1)
    }
  } else {
    ElMessage.error('取消关注失败')
  }
}

onMounted(async () => {
  const res = await getUserInfo(JSON.parse(localStorage.getItem('loginUser')).id)
  currentUser.value = res.data
  searchBlogs()
  // 获取关注列表
  fetchFollowingList()
})

// 组件销毁前清除编辑器
onBeforeUnmount(() => {
  const editor = editorRef.value
  if (editor == null) return
  editor.destroy()
})

// 编辑器创建回调
const handleCreated = (editor) => {
  editorRef.value = editor
}

// 拖动开始事件处理
const dragIndex = ref(null)
const dragId = ref(null) // 新增：记录拖动卡片的 id

// 修改：handleDragStart 函数，记录拖动卡片的 id
const handleDragStart = (index, id) => {
  dragIndex.value = index
  dragId.value = id
}

// 修改：handleDrop 函数，调用 deleteBlogById 接口
const handleDrop = async () => {
  if (dragIndex.value !== null && dragId.value !== null) {
    const res = await deleteBlogById(dragId.value)
    if (res.code) {
      blogs.value.splice(dragIndex.value, 1)
      searchBlogs()
      ElMessage.success('删除成功')
    } else {
      ElMessage.error('删除失败')
    }
    dragIndex.value = null
    dragId.value = null
  }
}
// 处理分页变化
const handlePageChange = (page) => {
  pagination.value.page = page
  searchBlogs()
}

// 处理页面大小变化
const handleSizeChange = (size) => {
  pagination.value.pageSize = size
  pagination.value.page = 1
  searchBlogs()
}
</script>

<template>
  <div class="home-container">
    <!-- 新增：搜索与新增按钮 -->
   

    <div class="top-section">
      <div class="user-section">
        <el-card class="user-profile-card">
          <div class="user-profile">
            <el-avatar class="user-avatar" :src="currentUser.image" :size="60" />
            <div class="user-info">
              <h2>{{ currentUser.username }}</h2>
              <!-- 展示自我介绍 -->
              <p v-if="currentUser.selfIntro" style="margin-top: 10px;">{{ currentUser.selfIntro }}</p>
              <!-- 编辑按钮 -->
              <el-button type="primary" @click="openEditDialog" style="margin-top: 10px;">编辑信息</el-button>
            </div>
          </div>
        </el-card>

        <!-- 新增：关注列表卡片 -->
        <el-card class="following-card">
          <div class="following-header">
            <h3>我的关注</h3>
            <span class="following-count">({{ followingList.length }})</span>
          </div>
          <div class="following-list">
            <div 
              v-for="user in followingList" 
              :key="user.id" 
              class="following-item"
            >
              <el-avatar :src="user.image" size="small" />
              <span class="following-username">{{ user.username }}</span>
              <el-button 
                type="primary" 
                size="small" 
                @click="followingStatus[user.id] ? handleUnfollowUser(user.id) : handleFollowUser(user.id)"
                class="follow-button"
                style="margin-left: auto;"
              >
                {{ followingStatus[user.id] ? '取消关注' : '关注' }}
              </el-button>
            </div>
            <div v-if="followingList.length === 0" class="no-following">
              暂无关注
            </div>
          </div>
        </el-card>

        <!-- 拖动删除区域移到右上角 -->
        <div
          class="trash-zone"
          @dragover.prevent
          @drop="handleDrop"
        >
          <p>将博客卡片拖到这里以删除</p>

        </div>
      </div>
    </div>

    <!-- 博客列表展示 -->
<el-card class="blog-list-card" style="margin-top: 30px; position: relative;">
  <div class="blog-header">
    <h3>我的博客</h3>
    <div class="blog-toolbar">
      <!-- 搜索按钮 -->
      <el-button 
        type="primary" 
        class="toolbar-btn"
        @click="showSearchForm = !showSearchForm"
      >
        <el-icon><component is="Search" /></el-icon>
      </el-button>

      <!-- 新增博客按钮 -->
      <el-button
        type="primary"
        style="margin-left: 10px;"
        @click="dialogVisibleAddBlog = true"
      >
        <el-icon><component is="Plus" /></el-icon>
      </el-button>
    </div>
  </div>
  
  <el-collapse-transition>
    <div v-show="showSearchForm" class="search-form-container">
      <el-card>
        <el-form :model="searchForm" label-width="80px" class="search-form">
          <el-row :gutter="20">
            <el-col :span="8">
              <el-form-item label="标题">
                <el-input v-model="searchForm.title" placeholder="请输入标题" />
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="内容">
                <el-input v-model="searchForm.content" placeholder="请输入内容" />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="8">
              <el-form-item label="开始时间">
                <el-date-picker
                  v-model="searchForm.startTime"
                  type="date"
                  placeholder="选择开始日期"
                  format="YYYY-MM-DD"
                  value-format="YYYY-MM-DD"
                  style="width: 100%"
                />
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="结束时间">
                <el-date-picker
                  v-model="searchForm.endTime"
                  type="date"
                  placeholder="选择结束日期"
                  format="YYYY-MM-DD"
                  value-format="YYYY-MM-DD"
                  style="width: 100%"
                />
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label=" ">
                <div class="form-actions">
                  <el-button type="primary" @click="searchBlogs">搜索</el-button>
                  <el-button @click="resetSearch">重置</el-button>
                </div>
              </el-form-item>
            </el-col>
          </el-row>
        </el-form>
      </el-card>
    </div>
  </el-collapse-transition>
  
  <!-- 始终显示博客列表区域，无论是否有博客 -->
  <div class="blog-list">
    <el-card 
      v-for="(blog, index) in blogs" 
      :key="index" 
      class="blog-card"
      @click="handleBlogClick(blog)"
      :style="{ backgroundColor: getRandomColor() }"
      draggable="true"
      @dragstart="handleDragStart(index, blog.id)"
    >
      <h4>{{ blog.title }}</h4>
      <p>发布时间：{{ blog.createTime }}</p>
    </el-card>
    
    <!-- 当没有博客时显示提示信息 -->
    <div v-if="blogs.length === 0" class="no-blogs-message">
      <p>暂无博客文章</p>
      <el-button type="primary" @click="dialogVisibleAddBlog = true" style="margin-top: 10px;">
        创建第一篇博客
      </el-button>
    </div>
  </div>
  
  <!-- 分页组件 -->
  <div class="pagination-container">
    <el-pagination
      v-model:current-page="pagination.page"
      v-model:page-size="pagination.pageSize"
      :page-sizes="12"
      :total="pagination.total"
      layout="total,sizes,prev, pager, next, jumper"
      @current-change="handlePageChange"
      @size-change="handleSizeChange"
    />
  </div>
</el-card>

    <!-- 新增博客对话框 -->
    <el-dialog v-model="dialogVisibleAddBlog" title="新增博客" width="70%" top="5vh">
      <el-form :model="newBlog" label-width="100px">
        <el-form-item label="标题">
          <el-input v-model="newBlog.title" />
        </el-form-item>
        <el-form-item label="内容">
          <!-- 富文本编辑器 -->
          <div style="border: 1px solid #ccc; z-index: 1000">
            <Toolbar
              style="border-bottom: 1px solid #ccc"
              :editor="editorRef"
              :defaultConfig="editorConfig"
              mode="default"
            />
            <Editor
              style="height: 400px; overflow-y: hidden;"
              :defaultConfig="editorConfig"
              mode="default"
              @onCreated="handleCreated"
            />
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisibleAddBlog = false">取消</el-button>
        <el-button type="primary" @click="submitBlog">提交</el-button>
      </template>
    </el-dialog>

    <!-- 编辑信息对话框 -->
    <el-dialog v-model="dialogVisible" title="编辑个人信息" width="30%">
      <el-form :model="editForm" label-width="120px">
        <el-form-item label="用户名">
          <el-input v-model="editForm.username" />
        </el-form-item>
        <el-form-item label="头像">
          <el-upload
            class="avatar-uploader"
            action="/api/user/upload"
            :show-file-list="false"
            :on-success="handleEditAvatarSuccess"
            :before-upload="beforeAvatarUpload"
          >
            <img v-if="editForm.image" :src="editForm.image" class="avatar" />
            <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
          </el-upload>
        </el-form-item>
        <el-form-item label="自我介绍">
          <el-input v-model="editForm.selfIntro" type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item>
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="saveUserInfo">保存</el-button>
        </el-form-item>
      </el-form>
    </el-dialog>
  </div>
</template>

<style scoped>
.home-container {
  padding: 20px;
  position: relative;
  padding-top: 30px; /* 增加顶部内边距，为按钮留出空间 */
}

.top-section {
  margin-bottom: 30px;
  display: flex;
  gap: 20px;
  flex-wrap: wrap;
  padding-right: 50px; /* 为右侧按钮留出空间，避免重叠 */
}

.user-section {
  display: flex;
  gap: 20px;
  flex-wrap: wrap;
  flex: 1;
}

.user-profile-card {
  flex: 1;
  min-width: 300px;
  max-width: calc(100% - 220px); /* 调整最大宽度，为拖拽区域留空间 */
  text-align: center;
}

/* 新增：关注列表卡片样式 */
.following-card {
  min-width: 200px;
  max-width: 250px;
}

.following-header {
  display: flex;
  align-items: center;
  margin-bottom: 15px;
}

.following-header h3 {
  margin: 0;
  font-size: 18px;
}

.following-count {
  margin-left: 8px;
  color: #999;
  font-size: 14px;
}

.following-list {
  max-height: 200px;
  overflow-y: auto;
}

.following-item {
  display: flex;
  align-items: center;
  padding: 8px 0;
}

.following-item .el-avatar {
  margin-right: 10px;
}

.following-username {
  font-size: 14px;
}

.follow-button {
  padding: 4px 8px;
  margin-left: auto;
}

.no-following {
  text-align: center;
  color: #999;
  padding: 20px 0;
}

/* 修改拖动删除区域为矩形并放在右上角 */
.trash-zone {
  min-width: 200px;
  height: 150px;
  border: 2px dashed #e4e4e4;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #f8f8f8;
  color: #888;
  font-size: 16px;
  text-align: center;
  border-radius: 8px;
  align-self: flex-start;
}

/* 修改博客卡片样式并定义动态网格布局 */
.blog-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 20px;
  justify-content: flex-start;
}

.blog-card {
  cursor: pointer;
  transition: transform 0.3s ease;
  min-width: 0;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.blog-card:hover {
  transform: scale(1.05);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
}

.blog-card h4 {
  margin: 0 0 12px 0;
  font-size: 18px;
  font-weight: bold;
  color: #333;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  line-clamp: 2; /* 标准属性，增强兼容性 */
}

.blog-card p {
  margin: 0;
  color: #777;
  font-size: 14px;
}

.avatar-uploader .avatar {
  width: 178px;
  height: 178px;
  display: block;
}

.search-form-container {
  margin-top: 30px;
  margin-bottom: 30px; /* 添加底部间距 */
  width: 100%;
}

.search-form {
  width: 100%;
}
.form-actions {
  display: flex;
  gap: 10px;
}

.blog-list-card {
  position: relative;
}

.blog-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.blog-toolbar {
  position: absolute;
  right: 20px;
  top: 20px;
  z-index: 100;
  display: flex;
}

.blogs-container {
  display: flex;
  flex-wrap: wrap;
  justify-content: flex-start;
  gap: 20px;
  align-items: center;
  margin-top: 60px;
}

.blogs-container.search-expanded {
  margin-top: 20px;
}
.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}

</style>

<style>
.el-icon.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 178px;
  height: 178px;
  text-align: center;
}
.no-blogs-message {
  grid-column: 1 / -1; /* 占据整行 */
  text-align: center;
  padding: 40px 20px;
  color: #999;
}

.no-blogs-message p {
  font-size: 18px;
  margin-bottom: 20px;
}

.w-e-text-container [data-slate-editor] p {
  margin: 0;
}
</style>