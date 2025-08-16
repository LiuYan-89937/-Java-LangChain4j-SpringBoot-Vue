<script setup>
import { ref, onMounted, onBeforeUnmount, shallowRef } from 'vue'
import { ElMessage } from 'element-plus'
import { addBlogs, searchBlogsByKey, getRandomBlogs, getSummary } from '@/api/blog'
import { useRouter } from 'vue-router'
// 引入 wangEditor
import { Editor, Toolbar } from '@wangeditor/editor-for-vue'
import '@wangeditor/editor/dist/css/style.css'

const router = useRouter()

// 用于存储博客列表数据
const blogs = ref([])
// 为每篇博客存储摘要
const blogSummaries = ref({})

// 控制新增博客对话框的显示
const dialogVisible = ref(false)

// 新增博客的数据对象
const newBlog = ref({
  title: '',
  content: '', // 富文本内容
})

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

// 控制搜索表单显示
const showSearchForm = ref(false)

// 添加用于跟踪展开状态的ref
const expandedCards = ref({})

// 添加用于跟踪定时器的ref
const hoverTimers = ref({})

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

// 搜索博客（关键词搜索）
const searchBlogs = async () => {
  try {
    const params = {
      page: pagination.value.page,
      pageSize: pagination.value.pageSize,
      title: searchForm.value.title,
      content: searchForm.value.content,
      username: searchForm.value.username,
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

// 重置搜索条件
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

onMounted(() => {
  searchBlogs()
})

// 打开新增博客对话框
const addBlog = () => {
  dialogVisible.value = true
  // 重置编辑器内容
  setTimeout(() => {
    if (editorRef.value) {
      editorRef.value.clear()
    }
  }, 100)
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
      dialogVisible.value = false
      newBlog.value.title = ''
      newBlog.value.content = ''
      pagination.value.page = 1
      await searchBlogs()
    } else {
      ElMessage.error('新增失败')
    }
  } catch (error) {
    ElMessage.error('新增失败: ' + error.message)
  }
}

// 新增点击卡片的事件处理函数
const handleBlogClick = (blog) => {
  router.push(`/blogs/${blog.id}/${blog.username}`)
}

// 获取随机博客
const fetchRandomBlog = async() => {
  try {
    const res = await getRandomBlogs()
    if (res.code) {
      blogs.value = res.data
    } else {
      ElMessage.error('获取随机博客失败')
    }
  } catch (error) {
    ElMessage.error('获取随机博客失败: ' + error.message)
  }
}

// 新增鼠标进入事件处理函数
const handleMouseEnter = (blog) => {
  const blogId = blog.id
  // 清除之前的定时器（如果有的话）
  if (hoverTimers.value[blogId]) {
    clearTimeout(hoverTimers.value[blogId])
  }
  
  // 设置新的定时器，1秒后展开卡片
  hoverTimers.value[blogId] = setTimeout(() => {
    expandedCards.value[blogId] = true
  }, 1000)
  getSummaryByContent(blog.content, blogId)
}

const handleMouseLeave = (blog) => {
  const blogId = blog.id
  // 清除定时器
  if (hoverTimers.value[blogId]) {
    clearTimeout(hoverTimers.value[blogId])
    delete hoverTimers.value[blogId]
  }
  
  // 收起卡片
  expandedCards.value[blogId] = false
}

const getSummaryByContent = async (content, blogId) => {

  blogSummaries.value[blogId] = '加载中'
  
  try {
    const res = await getSummary(content)
    // 根据API实际返回结构调整
    blogSummaries.value[blogId] =  res
  } catch (error) {
    console.error('获取摘要失败:', error)
    blogSummaries.value[blogId] = '摘要获取失败'
  }
}

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
</script>

<template>
  <el-container class="container">
    <el-container>
      <el-main class="main-content">
        <div class="toolbar">
          <el-button 
            type="primary" 
            class="toolbar-btn"
            @click="fetchRandomBlog"
          >
            <el-icon><component is="Refresh" /></el-icon>
          </el-button>
          
          <el-button 
            type="primary" 
            class="toolbar-btn"
            @click="showSearchForm = !showSearchForm"
          >
            <el-icon><component is="Search" /></el-icon>
          </el-button>
          
          <el-button type="primary" class="toolbar-btn" @click="addBlog">
            <el-icon><component is="Plus" /></el-icon>
          </el-button>

          <el-button type="primary" class="toolbar-btn" @click="resetSearch">
            <el-icon><component is="RefreshLeft" /></el-icon>
          </el-button>
        </div>

        <!-- 高级搜索表单 -->
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
                  <el-col :span="8">
                    <el-form-item label="作者">
                      <el-input v-model="searchForm.username" placeholder="请输入作者" />
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

        <div v-if="blogs.length > 0" class="blogs-container" :class="{ 'search-expanded': showSearchForm }">
          <div 
            v-for="blog in blogs" 
            :key="blog.id" 
            class="blog-card"
            @click="handleBlogClick(blog)"
            :class="[
              `color-${Math.floor(Math.random() * 5)}`,
              { 'expanded': expandedCards[blog.id] }
            ]"
            @mouseenter="handleMouseEnter(blog)"
            @mouseleave="handleMouseLeave(blog)"
          >
            <h2 class="blog-title">{{ blog.title }}</h2>
            <p class="blog-info">发布时间：{{ blog.createTime }}</p>
            <p class="blog-info">作者：{{ blog.username }}</p>
            <div v-if="expandedCards[blog.id]" class="blog-summary">
              <p class="summary-text">{{ blogSummaries[blog.id] || '加载中' }}</p>
            </div>
          </div>
        </div>
        <div v-else class="no-blogs">
          暂无博客文章
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

        <!-- 新增博客对话框 -->
        <el-dialog v-model="dialogVisible" title="新增博客" width="70%" top="5vh">
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
            <el-button @click="dialogVisible = false">取消</el-button>
            <el-button type="primary" @click="submitBlog">提交</el-button>
          </template>
        </el-dialog>
      </el-main>
    </el-container>
  </el-container>
</template>


<style scoped>
.w-e-text-container [data-slate-editor] p {
  margin: 0;
}
.container {
  height: 100vh;
  width: 100%;
}

.main-content {
  padding: 20px;
  position: relative;
  overflow-y: auto;
  height: 100vh;
}

.toolbar {
  display: flex;
  justify-content: center;
  gap: 10px;
  margin-bottom: 20px;
  position: relative;
  top: auto;
  right: auto;
}

.toolbar-btn {
  width: 40px;
  height: 40px;
  padding: 0;
  display: flex;
  align-items: center;
  justify-content: center;
}

.search-form-container {
  margin-top: 70px;
  width: 100%;
}

.search-form {
  width: 100%;
}

.form-actions {
  display: flex;
  gap: 10px;
}

.blogs-container {
  display: flex;
  flex-wrap: wrap;
  justify-content: flex-start;
  gap: 20px;
  align-items: center;
  margin-top: 60px;
  /* 修改为靠左对齐，但整体容器居中 */
  margin-left: auto;
  margin-right: auto;
  max-width: 100%; /* 修改最大宽度设置 */
  width: calc(25% * 4 + 20px * 3); /* 确保容器宽度正好容纳4列卡片 */
}

.blogs-container.search-expanded {
  margin-top: 20px;
}

.blog-card {
  flex: 0 1 calc(25% - 15px); /* 修改flex属性确保正好占1/4宽度 */
  margin-bottom: 20px;
  border: 1px solid #e4e4e4;
  border-radius: 8px;
  padding: 24px;
  background-color: #ffffff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  transition: transform 0.3s cubic-bezier(0.34, 1.56, 0.64, 1), box-shadow 0.3s ease;
  cursor: pointer;
  text-align: center;
  min-width: 200px;
  box-sizing: border-box; /* 添加盒模型计算方式 */
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  overflow: hidden; /* 防止内容溢出 */
  transform-origin: center top; /* 设置变换原点 */
}

/* 添加展开状态的样式 */
.blog-card.expanded {
  min-height: 200px; /* 展开时的最小高度 */
  animation: popIn 1.2s cubic-bezier(0.34, 1.56, 0.64, 1);
}

@keyframes popIn {
  0% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.05);
  }
  100% {
    transform: scale(1);
  }
}

.blog-title {
  margin: 0 0 12px 0;
  font-size: 22px;
  font-weight: bold;
  color: #333;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  line-clamp: 2; /* 标准属性，增强兼容性 */
}

.blog-info {
  margin: 0 0 10px 0;
  color: #777;
  font-size: 14px;
}

/* 添加摘要部分的样式 */
.blog-summary {
  margin-top: 15px;
  padding-top: 15px;
  border-top: 1px dashed #ccc;
  flex-grow: 1;
  text-align: left;
}

.summary-text {
  margin: 0;
  color: #555;
  font-size: 14px;
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 5;
  line-clamp: 5; /* 添加标准属性 */
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
}

.blog-card:hover {
  transform: scale(1.03);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
}

.blog-content {
  margin: 0;
  color: #333;
  text-align: left;
}

.no-blogs {
  font-size: 18px;
  color: #999;
  text-align: center;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}

.el-button[type="primary"] {
  background-color: #409EFF;
  border-color: #409EFF;
  transition: background-color 0.3s;
}

/* 定义博客卡片的背景色类 */
.color-0 {
  background-color: #FFDDC1;
}

.color-1 {
  background-color: #C1F0F6;
}

.color-2 {
  background-color: #FFE8F0;
}

.color-3 {
  background-color: #D4EDF9;
}

.color-4 {
  background-color: #E2F0CB;
}
</style>