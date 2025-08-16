<script setup>
import { ref, onMounted,computed } from 'vue'
import { useRoute } from 'vue-router'
import { getCommentsByBlogId,addHeartNum,getImageByUsername,addComment,deleteComment } from '@/api/comment'
import { getBlogInfro,getUserInfroByBlogId } from '@/api/blog'
import { followUserById,getFollowing,unfollowUserById } from '@/api/follow'
import { ElMessage } from 'element-plus'
const currentUser=JSON.parse(localStorage.getItem('loginUser'))
const route = useRoute()
const blog = ref(null)
// 添加评论数据
const comments = ref([])
const newCommentContent = ref('') // 新增评论内容
const blogUser = ref({})
const isFollowing = ref(false)
// 获取博客详情数据
const fetchBlogDetail = async (id) => {
  const res = await getCommentsByBlogId(id)
  
  // 处理评论数据，添加头像信息
  comments.value = await Promise.all(res.data.map(async (comment) => {
    try {
      const avatarRes = await getImageByUsername(comment.username)
      return {
        ...comment,
        avatar: avatarRes.data || '' // 假设返回的数据结构中有data字段包含图片URL
      }
    } catch (error) {
      // 如果获取头像失败，使用默认值
      return {
        ...comment,
        avatar: ''
      }
    }
  }))

  const res2 = await getBlogInfro(id)
  blog.value = res2.data
  const res3 = await getUserInfroByBlogId(id)
  blogUser.value = res3.data
  await checkFollow()
  
}

// 添加空的点赞处理函数
const handleLike = async (comment) => {
    const res = await addHeartNum(comment.id)
    comment.heartNum+=1
}

// 添加空的删除处理函数
const handleDelete =async (comment) => {
  const res = await deleteComment(comment.id)
  if (res.code) {
    ElMessage.success('删除成功')
    await fetchBlogDetail(route.params.id)
  } else {
    ElMessage.error('删除失败')
  }
}

// 添加关注处理函数
const handleFollow = async () => {
  // TODO: 实现关注逻辑
  const data = {
    userId: JSON.parse(localStorage.getItem('loginUser')).id,
    followId: blog.value.userId
  }
  const res = await followUserById(data)
  if (res.code) {
    ElMessage.success('关注成功')
    isFollowing.value = true
  } else {
    ElMessage.error('关注失败')
  }

}

// 添加取消关注处理函数
const handleUnfollow = async () => {
  const data = {
    userId: JSON.parse(localStorage.getItem('loginUser')).id,
    followId: blog.value.userId
  }
  const res = await unfollowUserById(data) // 假设同一个API可以处理取消关注
  if (res.code) {
    ElMessage.success('取消关注成功')
    isFollowing.value = false
  } else {
    ElMessage.error('取消关注失败')
  }
}

const checkFollow = async () => { 
  const res = await getFollowing(currentUser.id)
  for (let i = 0; i < res.data.length; i++) {
    if (res.data[i] === blogUser.value.id) {
      isFollowing.value = true
      return
    }
  }
}
// 提交新评论
const submitComment = async () => {
  if (!newCommentContent.value.trim()) return
  
  try {
    // 这里需要根据实际的 API 接口来实现
    // 假设有一个 addComment 方法用于提交评论
    const res = await addComment(route.params.id,currentUser.username,newCommentContent.value)
    // 提交成功后，将新评论添加到评论列表
    const newComment = res.data
    newCommentContent.value = '' // 清空输入框

     // 获取新评论的头像
    try {
      const avatarRes = await getImageByUsername(newComment.username)
      newComment.newField ='avatar'
      newComment.avatar = avatarRes.data
      fetchBlogDetail(route.params.id)

      
    } catch (error) {
      newComment.avatar = ''
    }
    comments.value.unshift(newComment)
  } catch (error) {
    console.error('提交评论失败:', error)
    // 可以添加错误提示
  }
}

onMounted(() => {
  const blogId = route.params.id
  fetchBlogDetail(blogId)
})
</script>

<template>
  <el-container v-if="blog" class="blog-container">
    <el-main>
      <el-card shadow="hover" class="blog-card">
        <template #header>
          <div class="blog-header">
            <h1>{{ blog.title }}</h1>
            <p class="publish-time">发布时间：{{ blog.createTime }}</p>
            <div class="blog-author-info">
              <span class="author-name">作者: {{blogUser.username }}</span>
              <el-button 
                type="primary" 
                size="small" 
                @click="isFollowing ? handleUnfollow() : handleFollow()"
                class="follow-button"
              >
                {{ isFollowing ? '取消关注' : '关注' }}
              </el-button>
            </div>
          </div>
        </template>
        <!-- 使用 v-html 渲染富文本内容 -->
        <div class="blog-content" v-html="blog.content"></div>
      </el-card>
      
      <!-- 添加评论区域 -->
      <el-card shadow="hover" class="comments-card">
        <template #header>
          <div class="comments-header">
            <h2>评论 ({{ comments.length }})</h2>
          </div>
        </template>
        <div class="add-comment-section">
  <div class="comment-input-container">
    <el-input
      v-model="newCommentContent"
      type="textarea"
      placeholder="请输入您的评论..."
      :rows="3"
      class="comment-textarea"
    />
    <div class="comment-actions-bar">
      <el-button 
        type="primary" 
        @click="submitComment"
        :disabled="!newCommentContent.trim()"
      >
        发表评论
      </el-button>
    </div>
  </div>
</div>
        <div class="comments-content">
          <div v-for="comment in comments" :key="comment.id" class="comment-item">
            <div class="comment-user">
              <!-- 修改为使用comment.avatar属性 -->
              <el-avatar 
                :size="50" 
                :src="comment.avatar" 
                class="user-avatar"
              >
                <!-- 当没有头像时显示用户名首字母 -->
                <span v-if="!comment.avatar">{{ comment.username.charAt(0) }}</span>
              </el-avatar>
              <div class="user-info">
                <span class="username">{{ comment.username }}</span>
                <span class="create-time">{{ comment.createTime }}</span>
              </div>
            </div>
            <div class="comment-content">
              {{ comment.content }}
            </div>
            <div class="comment-actions">
              <el-button type="primary" link @click="handleLike(comment)">
                <el-icon><Star /></el-icon>
                <span>{{ comment.heartNum }}</span>
              </el-button>
              <el-button 
                type="danger" 
                link 
                @click="handleDelete(comment)" 
                style="margin-left: 10px;"
                :disabled="currentUser.username !== comment.username && currentUser.username !== route.params.username"
              >
                <el-icon><Delete /></el-icon>
              </el-button>
            </div>
          </div>
          <div v-if="comments.length === 0" class="no-comments">
            暂无评论
          </div>
        </div>
      </el-card>
    </el-main>
  </el-container>
  <div v-else class="loading-container">
    <el-spinner />
    <span>加载中...</span>
  </div>
</template>

<style scoped>
/* 原有的样式 */
.blog-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
}

.blog-header h1 {
  margin: 0 0 10px 0;
}

.blog-author-info {
  display: flex;
  align-items: center;
  gap: 15px;
}

.author-name {
  font-size: 16px;
  color: #666;
}

.follow-button {
  padding: 6px 12px;
}

/* 移除原来的 white-space: pre-line，因为富文本内容已经有格式 */
.blog-content {
  line-height: 1.8;
}

.comment-item:last-child {
  border-bottom: none;
}

/* 移入的样式 */
.blog-container {
  height: 100vh;
  padding: 20px;
  background-color: #f5f7fa;
}

.blog-card {
  border-radius: 10px;
  overflow: hidden;
}

.blog-header h1 {
  margin: 20px 0 10px;
  font-size: 28px;
  color: #333;
}

.publish-time {
  color: #999;
  font-size: 14px;
}

/* 调整博客内容样式以适应富文本 */
.blog-content {
  padding: 20px;
  font-size: 16px;
  color: #555;
  background-color: #fff;
  border-top: 1px solid #eee;
  /* 以下样式用于处理富文本内容 */
  line-height: 1.8;
  word-wrap: break-word;
}

/* 富文本内容中的图片样式 */
.blog-content img {
  max-width: 100%;
  height: auto;
  border-radius: 8px;
  margin: 10px 0;
}

/* 富文本内容中的标题样式 */
.blog-content h1,
.blog-content h2,
.blog-content h3,
.blog-content h4,
.blog-content h5,
.blog-content h6 {
  margin: 16px 0 10px 0;
  color: #333;
}

.blog-content h1 {
  font-size: 28px;
}

.blog-content h2 {
  font-size: 24px;
}

.blog-content h3 {
  font-size: 20px;
}

/* 富文本内容中的段落样式 */
.blog-content p {
  margin: 0 0 12px 0;
}

/* 富文本内容中的链接样式 */
.blog-content a {
  color: #409eff;
  text-decoration: none;
}

.blog-content a:hover {
  text-decoration: underline;
}

/* 富文本内容中的代码块样式 */
.blog-content pre {
  background-color: #f5f7fa;
  padding: 15px;
  border-radius: 6px;
  overflow-x: auto;
  margin: 15px 0;
  font-family: 'Courier New', monospace;
}

.blog-content code {
  background-color: #f5f7fa;
  padding: 2px 6px;
  border-radius: 4px;
  font-family: 'Courier New', monospace;
  font-size: 14px;
}

.comments-card {
  border-radius: 10px;
  overflow: hidden;
  margin-top: 20px;
}

.comments-header h2 {
  margin: 0;
  font-size: 22px;
  color: #333;
}

.comments-content {
  padding: 20px;
  background-color: #fff;
}

.comment-item {
  padding: 15px 0;
  border-bottom: 1px solid #eee;
}

.comment-user {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}

.user-avatar {
  margin-right: 10px;
}

.user-info {
  display: flex;
  flex-direction: column;
}

.username {
  font-weight: bold;
  color: #409eff;
}

.create-time {
  margin-top: 4px;
  color: #999;
  font-size: 12px;
}

.comment-content {
  margin-left: 60px;
  color: #555;
  line-height: 1.6;
}

.comment-actions {
  margin-left: 60px;
  margin-top: 10px;
  display: flex;
  justify-content: flex-end;
}

.no-comments {
  text-align: center;
  padding: 20px;
  color: #999;
}

.loading-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
}

.loading-container .el-spinner {
  width: 40px;
  height: 40px;
}

.loading-container span {
  margin-left: 10px;
  font-size: 16px;
}

.add-comment-section {
  padding: 20px;
  border-bottom: 1px solid #eee;
}

.comment-input-container {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.comment-textarea :deep(.el-textarea__inner) {
  resize: none;
}

.comment-actions-bar {
  display: flex;
  justify-content: flex-end;
}
</style>