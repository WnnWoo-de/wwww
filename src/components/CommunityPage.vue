<template>
  <div class="community-page">
    <!-- Page Header -->
    <div class="page-header">
      <h1>社区故事墙</h1>
      <p>分享你的志愿故事，传递温暖与感动</p>
    </div>

    <!-- Story Publish Section -->
    <div class="publish-section">
      <div class="publish-container">
        <div class="publish-card">
          <div class="publish-header">
            <div class="user-avatar">
              <img src="https://via.placeholder.com/50x50?text=我" alt="用户头像">
            </div>
            <div class="publish-prompt">
              <span>分享你的志愿故事...</span>
            </div>
          </div>
          
          <div v-if="showPublishForm" class="publish-form">
            <textarea 
              v-model="newStory.content" 
              placeholder="写下你的志愿故事，分享感动瞬间..."
              class="story-textarea"
              rows="4"
            ></textarea>
            
            <div class="publish-options">
              <div class="options-left">
                <button class="option-btn" @click="addImage">
                  <span class="option-icon">📷</span>
                  <span>添加图片</span>
                </button>
                <button class="option-btn" @click="addLocation">
                  <span class="option-icon">📍</span>
                  <span>添加位置</span>
                </button>
                <button class="option-btn" @click="addActivity">
                  <span class="option-icon">🎯</span>
                  <span>关联活动</span>
                </button>
              </div>
              
              <div class="options-right">
                <button class="btn btn-secondary" @click="cancelPublish">取消</button>
                <button class="btn btn-primary" @click="publishStory" :disabled="!newStory.content.trim()">发布</button>
              </div>
            </div>
            
            <div v-if="newStory.images.length > 0" class="preview-images">
              <div v-for="(image, index) in newStory.images" :key="index" class="preview-image">
                <img :src="image" alt="预览图片">
                <button class="remove-image" @click="removeImage(index)">×</button>
              </div>
            </div>
            
            <div v-if="newStory.location" class="preview-location">
              <span class="location-icon">📍</span>
              <span class="location-text">{{ newStory.location }}</span>
              <button class="remove-location" @click="removeLocation">×</button>
            </div>
            
            <div v-if="newStory.activity" class="preview-activity">
              <span class="activity-icon">🎯</span>
              <span class="activity-text">{{ newStory.activity }}</span>
              <button class="remove-activity" @click="removeActivity">×</button>
            </div>
          </div>
          
          <div v-else class="publish-trigger" @click="showPublishForm = true">
            <span>点击分享你的志愿故事...</span>
          </div>
        </div>
      </div>
    </div>

    <!-- Filter and Sort -->
    <div class="filter-section">
      <div class="filter-container">
        <div class="filter-tabs">
          <button 
            v-for="tab in filterTabs" 
            :key="tab.id"
            :class="['filter-tab', { active: selectedFilter === tab.id }]"
            @click="selectedFilter = tab.id"
          >
            <span class="tab-icon">{{ tab.icon }}</span>
            <span class="tab-text">{{ tab.name }}</span>
          </button>
        </div>
        
        <div class="sort-options">
          <select v-model="sortBy" class="sort-select">
            <option value="latest">最新发布</option>
            <option value="popular">最受欢迎</option>
            <option value="comments">评论最多</option>
          </select>
        </div>
      </div>
    </div>

    <!-- Stories Feed -->
    <div class="stories-container">
      <div class="stories-feed">
        <div 
          v-for="story in filteredStories" 
          :key="story.id" 
          class="story-card"
        >
          <!-- Story Header -->
          <div class="story-header">
            <div class="author-info">
              <img :src="story.author.avatar" :alt="story.author.name" class="author-avatar">
              <div class="author-details">
                <h4 class="author-name">{{ story.author.name }}</h4>
                <div class="story-meta">
                  <span class="story-time">{{ formatTime(story.createdAt) }}</span>
                  <span v-if="story.location" class="story-location">
                    <span class="location-icon">📍</span>
                    {{ story.location }}
                  </span>
                </div>
              </div>
            </div>
            
            <div class="story-actions">
              <button class="action-btn" @click="toggleMenu(story.id)">
                <span>⋯</span>
              </button>
            </div>
          </div>
          
          <!-- Story Content -->
          <div class="story-content">
            <p class="story-text">{{ story.content }}</p>
            
            <div v-if="story.activity" class="related-activity">
              <div class="activity-tag">
                <span class="activity-icon">🎯</span>
                <span class="activity-name">{{ story.activity }}</span>
              </div>
            </div>
            
            <div v-if="story.images && story.images.length > 0" class="story-images">
              <div 
                v-for="(image, index) in story.images" 
                :key="index" 
                class="story-image"
                @click="openImageViewer(story.images, index)"
              >
                <img :src="image" :alt="`故事图片 ${index + 1}`">
              </div>
            </div>
          </div>
          
          <!-- Story Footer -->
          <div class="story-footer">
            <div class="story-stats">
              <button 
                :class="['stat-btn', { active: story.isLiked }]"
                @click="toggleLike(story)"
              >
                <span class="stat-icon">{{ story.isLiked ? '❤️' : '🤍' }}</span>
                <span class="stat-count">{{ story.likes }}</span>
              </button>
              
              <button class="stat-btn" @click="toggleComments(story.id)">
                <span class="stat-icon">💬</span>
                <span class="stat-count">{{ story.comments.length }}</span>
              </button>
              
              <button class="stat-btn" @click="shareStory(story)">
                <span class="stat-icon">📤</span>
                <span class="stat-text">分享</span>
              </button>
            </div>
          </div>
          
          <!-- Comments Section -->
          <div v-if="story.showComments" class="comments-section">
            <div class="comments-list">
              <div 
                v-for="comment in story.comments" 
                :key="comment.id" 
                class="comment-item"
              >
                <img :src="comment.author.avatar" :alt="comment.author.name" class="comment-avatar">
                <div class="comment-content">
                  <div class="comment-header">
                    <span class="comment-author">{{ comment.author.name }}</span>
                    <span class="comment-time">{{ formatTime(comment.createdAt) }}</span>
                  </div>
                  <p class="comment-text">{{ comment.content }}</p>
                </div>
              </div>
            </div>
            
            <div class="comment-input">
              <img src="https://via.placeholder.com/40x40?text=我" alt="我的头像" class="my-avatar">
              <div class="input-container">
                <input 
                  v-model="commentInputs[story.id]" 
                  type="text" 
                  placeholder="写下你的评论..."
                  class="comment-field"
                  @keyup.enter="addComment(story)"
                >
                <button 
                  class="comment-submit"
                  @click="addComment(story)"
                  :disabled="!commentInputs[story.id]?.trim()"
                >
                  发送
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
      
      <!-- Empty State -->
      <div v-if="filteredStories.length === 0" class="empty-state">
        <div class="empty-icon">📝</div>
        <h3>还没有故事</h3>
        <p>成为第一个分享志愿故事的人吧！</p>
        <button class="btn btn-primary" @click="showPublishForm = true">分享故事</button>
      </div>
    </div>

    <!-- Image Viewer Modal -->
    <div v-if="imageViewer.show" class="image-viewer-overlay" @click="closeImageViewer">
      <div class="image-viewer" @click.stop>
        <button class="viewer-close" @click="closeImageViewer">×</button>
        <button v-if="imageViewer.currentIndex > 0" class="viewer-prev" @click="prevImage">‹</button>
        <img :src="imageViewer.images[imageViewer.currentIndex]" alt="查看图片" class="viewer-image">
        <button v-if="imageViewer.currentIndex < imageViewer.images.length - 1" class="viewer-next" @click="nextImage">›</button>
        <div class="viewer-indicator">
          {{ imageViewer.currentIndex + 1 }} / {{ imageViewer.images.length }}
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, reactive, defineEmits } from 'vue'

const emit = defineEmits(['show-login'])

// 发布相关状态
const showPublishForm = ref(false)
const newStory = reactive({
  content: '',
  images: [],
  location: '',
  activity: ''
})

// 筛选和排序
const selectedFilter = ref('all')
const sortBy = ref('latest')

// 评论输入
const commentInputs = ref({})

// 图片查看器
const imageViewer = reactive({
  show: false,
  images: [],
  currentIndex: 0
})

// 筛选标签
const filterTabs = ref([
  { id: 'all', name: '全部', icon: '📚' },
  { id: 'recent', name: '最新', icon: '🆕' },
  { id: 'popular', name: '热门', icon: '🔥' },
  { id: 'following', name: '关注', icon: '👥' }
])

// 模拟故事数据
const stories = ref([
  {
    id: 1,
    author: {
      id: 1,
      name: '张小明',
      avatar: 'https://images.unsplash.com/photo-1599566150163-29194dcaad36?q=80&w=687&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D'
    },
    content: '今天参加了社区的环保清洁活动，看到公园变得干净整洁，心里特别有成就感。虽然累了一天，但看到大家的笑容，觉得一切都值得。环保从我做起，让我们的家园更美好！',
    images: [
      'https://ts1.tc.mm.bing.net/th/id/OIP-C.C3oCXAPoZFYdboNpGVdjgwHaE3?r=0&rs=1&pid=ImgDetMain&o=7&rm=3',
      'https://p9.itc.cn/images01/20201009/b72f9f4462a74f108b3622173da4d4d2.jpeg'
    ],
    location: '滨江公园',
    activity: '环保清洁行动',
    likes: 24,
    isLiked: false,
    createdAt: '2024-01-15T10:30:00Z',
    showComments: false,
    comments: [
      {
        id: 1,
        author: { name: '李小红', avatar: 'https://images.unsplash.com/photo-1438761681033-6461ffad8d80?q=80&w=1170&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D' },
        content: '太棒了！为你点赞！',
        createdAt: '2024-01-15T11:00:00Z'
      },
      {
        id: 2,
        author: { name: '王大伟', avatar: 'https://ts1.tc.mm.bing.net/th/id/OIP-C.UyaBji0AU_6M3VDA2F1RvgAAAA?r=0&rs=1&pid=ImgDetMain&o=7&rm=3' },
        content: '我也想参加这样的活动',
        createdAt: '2024-01-15T11:15:00Z'
      }
    ]
  },
  {
    id: 2,
    author: {
      id: 2,
      name: '李小红',
      avatar: 'https://images.unsplash.com/photo-1438761681033-6461ffad8d80?q=80&w=1170&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D'
    },
    content: '第一次去养老院做志愿服务，和爷爷奶奶们聊天，听他们讲过去的故事。他们的人生阅历让我受益匪浅，也让我明白了陪伴的珍贵。下次还要去！',
    images: [
      'https://ts2.tc.mm.bing.net/th/id/OIP-C.a7MFXh6x7LWxV79X6Wtx6gHaD-?r=0&rs=1&pid=ImgDetMain&o=7&rm=3'
    ],
    location: '阳光养老院',
    activity: '关爱空巢老人志愿服务',
    likes: 18,
    isLiked: true,
    createdAt: '2024-01-14T16:20:00Z',
    showComments: false,
    comments: [
      {
        id: 3,
        author: { name: '张小明', avatar: 'https://via.placeholder.com/40x40?text=张' },
        content: '很有意义的活动！',
        createdAt: '2024-01-14T17:00:00Z'
      }
    ]
  },
  {
    id: 3,
    author: {
      id: 3,
      name: '王大伟',
      avatar: 'https://ts1.tc.mm.bing.net/th/id/OIP-C.UyaBji0AU_6M3VDA2F1RvgAAAA?r=0&rs=1&pid=ImgDetMain&o=7&rm=3'
    },
    content: '今天在线给山区的孩子们上编程课，看到他们对知识的渴望和学习的热情，真的很感动。虽然是线上教学，但能感受到他们的认真和努力。希望能帮助更多的孩子接触到科技知识。',
    images: [],
    location: '在线',
    activity: '线上编程教学',
    likes: 32,
    isLiked: false,
    createdAt: '2024-01-13T14:45:00Z',
    showComments: false,
    comments: [
      {
        id: 4,
        author: { name: '刘小芳', avatar: 'https://pic.huitu.com/pic/20220421/1887823_20220421112005053020_0.jpg' },
        content: '教育改变命运，你做的很棒！',
        createdAt: '2024-01-13T15:30:00Z'
      },
      {
        id: 5,
        author: { name: '陈小华', avatar: 'https://via.placeholder.com/40x40?text=陈' },
        content: '我也想参与这样的教学活动',
        createdAt: '2024-01-13T16:00:00Z'
      }
    ]
  },
  {
    id: 4,
    author: {
      id: 4,
      name: '刘小芳',
      avatar: 'https://images.unsplash.com/photo-1580489944761-15a19d654956?w=600&auto=format&fit=crop&q=60&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8OHx8cGVyc29ufGVufDB8fDB8fHww'
    },
    content: '参加了社区义诊活动，虽然我不是医生，但能帮助维持秩序、引导居民，也是很有意义的。看到医生们免费为大家检查身体，真的很感动。健康是最重要的财富！',
    images: [
      'https://ts2.tc.mm.bing.net/th/id/OIP-C.SJ3xc453kcRFFMPrjyyk4gHaFj?r=0&rs=1&pid=ImgDetMain&o=7&rm=3'
    ],
    location: '和谐社区',
    activity: '社区义诊服务',
    likes: 15,
    isLiked: false,
    createdAt: '2024-01-12T09:15:00Z',
    showComments: false,
    comments: []
  }
])

// 筛选后的故事
const filteredStories = computed(() => {
  let filtered = [...stories.value]
  
  // 根据筛选条件过滤
  switch (selectedFilter.value) {
    case 'recent':
      filtered = filtered.filter(story => {
        const daysDiff = (new Date() - new Date(story.createdAt)) / (1000 * 60 * 60 * 24)
        return daysDiff <= 7
      })
      break
    case 'popular':
      filtered = filtered.filter(story => story.likes >= 20)
      break
    case 'following':
      // 这里应该根据用户关注列表过滤
      filtered = filtered.filter(story => [1, 2].includes(story.author.id))
      break
  }
  
  // 根据排序条件排序
  switch (sortBy.value) {
    case 'latest':
      filtered.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt))
      break
    case 'popular':
      filtered.sort((a, b) => b.likes - a.likes)
      break
    case 'comments':
      filtered.sort((a, b) => b.comments.length - a.comments.length)
      break
  }
  
  return filtered
})

// 方法
const formatTime = (dateString) => {
  const date = new Date(dateString)
  const now = new Date()
  const diffMs = now - date
  const diffMins = Math.floor(diffMs / (1000 * 60))
  const diffHours = Math.floor(diffMs / (1000 * 60 * 60))
  const diffDays = Math.floor(diffMs / (1000 * 60 * 60 * 24))
  
  if (diffMins < 60) {
    return `${diffMins}分钟前`
  } else if (diffHours < 24) {
    return `${diffHours}小时前`
  } else if (diffDays < 7) {
    return `${diffDays}天前`
  } else {
    return date.toLocaleDateString('zh-CN')
  }
}

const addImage = () => {
  // 模拟添加图片
  const imageUrl = `https://via.placeholder.com/400x300?text=图片${newStory.images.length + 1}`
  newStory.images.push(imageUrl)
}

const removeImage = (index) => {
  newStory.images.splice(index, 1)
}

const addLocation = () => {
  // 模拟添加位置
  newStory.location = '当前位置'
}

const removeLocation = () => {
  newStory.location = ''
}

const addActivity = () => {
  // 模拟关联活动
  newStory.activity = '环保清洁行动'
}

const removeActivity = () => {
  newStory.activity = ''
}

const publishStory = () => {
  if (!newStory.content.trim()) return
  
  // 创建新故事
  const story = {
    id: Date.now(),
    author: {
      id: 999,
      name: '我',
      avatar: 'https://via.placeholder.com/50x50?text=我'
    },
    content: newStory.content,
    images: [...newStory.images],
    location: newStory.location,
    activity: newStory.activity,
    likes: 0,
    isLiked: false,
    createdAt: new Date().toISOString(),
    showComments: false,
    comments: []
  }
  
  stories.value.unshift(story)
  
  // 重置表单
  newStory.content = ''
  newStory.images = []
  newStory.location = ''
  newStory.activity = ''
  showPublishForm.value = false
}

const cancelPublish = () => {
  newStory.content = ''
  newStory.images = []
  newStory.location = ''
  newStory.activity = ''
  showPublishForm.value = false
}

const toggleLike = (story) => {
  story.isLiked = !story.isLiked
  story.likes += story.isLiked ? 1 : -1
}

const toggleComments = (storyId) => {
  const story = stories.value.find(s => s.id === storyId)
  if (story) {
    story.showComments = !story.showComments
  }
}

const addComment = (story) => {
  const content = commentInputs.value[story.id]?.trim()
  if (!content) return
  
  const comment = {
    id: Date.now(),
    author: {
      name: '我',
      avatar: 'https://via.placeholder.com/40x40?text=我'
    },
    content,
    createdAt: new Date().toISOString()
  }
  
  story.comments.push(comment)
  commentInputs.value[story.id] = ''
}

const shareStory = (story) => {
  // 模拟分享功能
  alert(`分享故事：${story.content.substring(0, 50)}...`)
}

const toggleMenu = (storyId) => {
  // 显示更多操作菜单
  console.log('Toggle menu for story:', storyId)
}

const openImageViewer = (images, index) => {
  imageViewer.images = images
  imageViewer.currentIndex = index
  imageViewer.show = true
}

const closeImageViewer = () => {
  imageViewer.show = false
}

const prevImage = () => {
  if (imageViewer.currentIndex > 0) {
    imageViewer.currentIndex--
  }
}

const nextImage = () => {
  if (imageViewer.currentIndex < imageViewer.images.length - 1) {
    imageViewer.currentIndex++
  }
}
</script>

<style scoped>
.community-page {
  min-height: 100vh;
  background: #f8f9fa;
}

.page-header {
  background: linear-gradient(135deg, #a8e6cf 0%, #88d8a3 100%);
  color: white;
  text-align: center;
  padding: 60px 20px;
}

.page-header h1 {
  font-size: 2.5rem;
  margin-bottom: 10px;
}

.page-header p {
  font-size: 1.1rem;
  opacity: 0.9;
}

.publish-section {
  background: white;
  padding: 20px;
  box-shadow: 0 2px 10px rgba(0,0,0,0.1);
}

.publish-container {
  max-width: 600px;
  margin: 0 auto;
}

.publish-card {
  background: #f8f9fa;
  border-radius: 15px;
  padding: 20px;
}

.publish-header {
  display: flex;
  align-items: center;
  gap: 15px;
  margin-bottom: 15px;
}

.user-avatar img {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  object-fit: cover;
}

.publish-prompt {
  flex: 1;
  color: #666;
  font-size: 1rem;
}

.publish-trigger {
  padding: 15px;
  background: white;
  border-radius: 10px;
  cursor: pointer;
  color: #666;
  transition: all 0.3s ease;
}

.publish-trigger:hover {
  background: #f0f0f0;
}

.publish-form {
  background: white;
  border-radius: 10px;
  padding: 15px;
}

.story-textarea {
  width: 100%;
  border: none;
  outline: none;
  resize: vertical;
  font-size: 1rem;
  line-height: 1.5;
  font-family: inherit;
}

.publish-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 15px;
  padding-top: 15px;
  border-top: 1px solid #eee;
}

.options-left {
  display: flex;
  gap: 10px;
}

.option-btn {
  display: flex;
  align-items: center;
  gap: 5px;
  padding: 8px 12px;
  border: none;
  background: #f0f0f0;
  border-radius: 20px;
  cursor: pointer;
  font-size: 0.9rem;
  transition: all 0.3s ease;
}

.option-btn:hover {
  background: #e0e0e0;
}

.options-right {
  display: flex;
  gap: 10px;
}

.btn {
  padding: 8px 16px;
  border: none;
  border-radius: 20px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.btn-secondary {
  background: #6c757d;
  color: white;
}

.btn-primary {
  background: #a8e6cf;
  color: white;
}

.btn-primary:disabled {
  background: #ccc;
  cursor: not-allowed;
}

.preview-images {
  display: flex;
  gap: 10px;
  margin-top: 15px;
  flex-wrap: wrap;
}

.preview-image {
  position: relative;
  width: 80px;
  height: 80px;
  border-radius: 8px;
  overflow: hidden;
}

.preview-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.remove-image {
  position: absolute;
  top: 5px;
  right: 5px;
  width: 20px;
  height: 20px;
  border: none;
  background: rgba(0,0,0,0.7);
  color: white;
  border-radius: 50%;
  cursor: pointer;
  font-size: 12px;
}

.preview-location,
.preview-activity {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-top: 10px;
  padding: 8px 12px;
  background: #e3f2fd;
  border-radius: 20px;
  font-size: 0.9rem;
}

.remove-location,
.remove-activity {
  margin-left: auto;
  border: none;
  background: none;
  cursor: pointer;
  color: #666;
}

.filter-section {
  background: white;
  padding: 20px;
  border-bottom: 1px solid #eee;
}

.filter-container {
  max-width: 800px;
  margin: 0 auto;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.filter-tabs {
  display: flex;
  gap: 10px;
}

.filter-tab {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 16px;
  border: 2px solid #e1e5e9;
  border-radius: 20px;
  background: white;
  cursor: pointer;
  transition: all 0.3s ease;
}

.filter-tab:hover {
  border-color: #a8e6cf;
}

.filter-tab.active {
  border-color: #a8e6cf;
  background: #a8e6cf;
  color: white;
}

.sort-select {
  padding: 8px 12px;
  border: 1px solid #ddd;
  border-radius: 20px;
  background: white;
  cursor: pointer;
  outline: none;
}

.stories-container {
  max-width: 600px;
  margin: 0 auto;
  padding: 20px;
}

.stories-feed {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.story-card {
  background: white;
  border-radius: 15px;
  padding: 20px;
  box-shadow: 0 2px 10px rgba(0,0,0,0.08);
}

.story-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 15px;
}

.author-info {
  display: flex;
  gap: 12px;
}

.author-avatar {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  object-fit: cover;
}

.author-details {
  flex: 1;
}

.author-name {
  font-size: 1rem;
  font-weight: 600;
  margin-bottom: 5px;
  color: #333;
}

.story-meta {
  display: flex;
  gap: 15px;
  font-size: 0.9rem;
  color: #666;
}

.story-location {
  display: flex;
  align-items: center;
  gap: 4px;
}

.action-btn {
  border: none;
  background: none;
  cursor: pointer;
  padding: 5px;
  color: #666;
  font-size: 1.2rem;
}

.story-content {
  margin-bottom: 15px;
}

.story-text {
  line-height: 1.6;
  color: #333;
  margin-bottom: 15px;
}

.related-activity {
  margin-bottom: 15px;
}

.activity-tag {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 6px 12px;
  background: #fff3cd;
  border-radius: 15px;
  font-size: 0.9rem;
  color: #856404;
}

.story-images {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 10px;
  margin-bottom: 15px;
}

.story-image {
  border-radius: 10px;
  overflow: hidden;
  cursor: pointer;
  transition: transform 0.3s ease;
}

.story-image:hover {
  transform: scale(1.02);
}

.story-image img {
  width: 100%;
  height: 200px;
  object-fit: cover;
}

.story-footer {
  border-top: 1px solid #f0f0f0;
  padding-top: 15px;
}

.story-stats {
  display: flex;
  gap: 20px;
}

.stat-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  border: none;
  background: none;
  cursor: pointer;
  padding: 8px 12px;
  border-radius: 20px;
  transition: all 0.3s ease;
  color: #666;
}

.stat-btn:hover {
  background: #f0f0f0;
}

.stat-btn.active {
  color: #ff6b6b;
}

.stat-icon {
  font-size: 1.1rem;
}

.stat-count,
.stat-text {
  font-size: 0.9rem;
}

.comments-section {
  margin-top: 15px;
  padding-top: 15px;
  border-top: 1px solid #f0f0f0;
}

.comments-list {
  margin-bottom: 15px;
}

.comment-item {
  display: flex;
  gap: 10px;
  margin-bottom: 15px;
}

.comment-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
}

.comment-content {
  flex: 1;
  background: #f8f9fa;
  padding: 10px 15px;
  border-radius: 15px;
}

.comment-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 5px;
}

.comment-author {
  font-weight: 600;
  font-size: 0.9rem;
  color: #333;
}

.comment-time {
  font-size: 0.8rem;
  color: #666;
}

.comment-text {
  font-size: 0.9rem;
  line-height: 1.4;
  color: #333;
}

.comment-input {
  display: flex;
  gap: 10px;
  align-items: center;
}

.my-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
}

.input-container {
  flex: 1;
  display: flex;
  gap: 10px;
}

.comment-field {
  flex: 1;
  padding: 10px 15px;
  border: 1px solid #ddd;
  border-radius: 20px;
  outline: none;
}

.comment-submit {
  padding: 10px 20px;
  border: none;
  background: #a8e6cf;
  color: white;
  border-radius: 20px;
  cursor: pointer;
  font-weight: 600;
}

.comment-submit:disabled {
  background: #ccc;
  cursor: not-allowed;
}

.empty-state {
  text-align: center;
  padding: 60px 20px;
  color: #666;
}

.empty-icon {
  font-size: 4rem;
  margin-bottom: 20px;
}

.empty-state h3 {
  margin-bottom: 10px;
}

.empty-state p {
  margin-bottom: 20px;
}

/* Image Viewer */
.image-viewer-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0,0,0,0.9);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 2000;
}

.image-viewer {
  position: relative;
  max-width: 90vw;
  max-height: 90vh;
}

.viewer-image {
  max-width: 100%;
  max-height: 100%;
  object-fit: contain;
}

.viewer-close {
  position: absolute;
  top: -50px;
  right: 0;
  background: none;
  border: none;
  color: white;
  font-size: 2rem;
  cursor: pointer;
}

.viewer-prev,
.viewer-next {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  background: rgba(255,255,255,0.2);
  border: none;
  color: white;
  font-size: 2rem;
  padding: 10px 15px;
  cursor: pointer;
  border-radius: 5px;
}

.viewer-prev {
  left: -60px;
}

.viewer-next {
  right: -60px;
}

.viewer-indicator {
  position: absolute;
  bottom: -40px;
  left: 50%;
  transform: translateX(-50%);
  color: white;
  font-size: 0.9rem;
}

/* Responsive */
@media (max-width: 768px) {
  .filter-container {
    flex-direction: column;
    gap: 15px;
  }
  
  .filter-tabs {
    flex-wrap: wrap;
    justify-content: center;
  }
  
  .publish-options {
    flex-direction: column;
    gap: 15px;
    align-items: stretch;
  }
  
  .options-left {
    justify-content: center;
  }
  
  .story-images {
    grid-template-columns: 1fr;
  }
  
  .story-stats {
    justify-content: space-around;
  }
  
  .viewer-prev {
    left: 10px;
  }
  
  .viewer-next {
    right: 10px;
  }
  
  .viewer-close {
    top: 10px;
    right: 10px;
  }
}
</style>