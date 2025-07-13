<template>
  <div class="store-page">
    <!-- Page Header -->
    <div class="page-header">
      <h1>积分商城</h1>
      <p>用爱心积分兑换精美礼品，让善行得到回报</p>
      
      <div class="user-points">
        <div class="points-display">
          <span class="points-icon">💎</span>
          <span class="points-text">我的积分</span>
          <span class="points-value">{{ userPoints }}</span>
        </div>
      </div>
    </div>

    <!-- Categories Filter -->
    <div class="categories-section">
      <div class="categories-container">
        <button 
          v-for="category in categories" 
          :key="category.id"
          :class="['category-btn', { active: selectedCategory === category.id }]"
          @click="selectedCategory = category.id"
        >
          <span class="category-icon">{{ category.icon }}</span>
          <span class="category-name">{{ category.name }}</span>
        </button>
      </div>
    </div>

    <!-- Products Grid -->
    <div class="products-container">
      <div class="products-grid">
        <div 
          v-for="product in filteredProducts" 
          :key="product.id" 
          class="product-card"
          @click="selectProduct(product)"
        >
          <div class="product-image">
            <img :src="product.image" :alt="product.name">
            <div v-if="product.stock <= 5" class="stock-warning">仅剩{{ product.stock }}件</div>
            <div v-if="product.isHot" class="hot-badge">🔥 热门</div>
          </div>
          
          <div class="product-content">
            <h3 class="product-name">{{ product.name }}</h3>
            <p class="product-description">{{ product.description }}</p>
            
            <div class="product-meta">
              <div class="product-points">
                <span class="points-required">{{ product.points }}</span>
                <span class="points-unit">积分</span>
              </div>
              
              <div class="product-stock">
                <span class="stock-text">库存：{{ product.stock }}</span>
              </div>
            </div>
            
            <div class="product-rating">
              <div class="stars">
                <span v-for="i in 5" :key="i" :class="['star', { filled: i <= product.rating }]">⭐</span>
              </div>
              <span class="rating-text">({{ product.reviews }}条评价)</span>
            </div>
            
            <button 
              class="btn btn-exchange"
              :disabled="!canExchange(product)"
              @click.stop="exchangeProduct(product)"
            >
              {{ getExchangeButtonText(product) }}
            </button>
          </div>
        </div>
      </div>
      
      <!-- Empty State -->
      <div v-if="filteredProducts.length === 0" class="empty-state">
        <div class="empty-icon">🛍️</div>
        <h3>该分类暂无商品</h3>
        <p>请选择其他分类查看更多商品</p>
      </div>
    </div>

    <!-- Product Detail Modal -->
    <div v-if="selectedProductDetail" class="modal-overlay" @click="closeModal">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h2>{{ selectedProductDetail.name }}</h2>
          <button class="close-btn" @click="closeModal">×</button>
        </div>
        
        <div class="modal-body">
          <div class="product-detail-image">
            <img :src="selectedProductDetail.image" :alt="selectedProductDetail.name">
          </div>
          
          <div class="product-details">
            <p class="detail-description">{{ selectedProductDetail.fullDescription }}</p>
            
            <div class="detail-info">
              <div class="info-row">
                <strong>所需积分：</strong>{{ selectedProductDetail.points }} 分
              </div>
              <div class="info-row">
                <strong>剩余库存：</strong>{{ selectedProductDetail.stock }} 件
              </div>
              <div class="info-row">
                <strong>商品评分：</strong>
                <span class="rating-stars">
                  <span v-for="i in 5" :key="i" :class="['star', { filled: i <= selectedProductDetail.rating }]">⭐</span>
                </span>
                {{ selectedProductDetail.rating }}/5.0
              </div>
              <div class="info-row">
                <strong>配送方式：</strong>{{ selectedProductDetail.shipping }}
              </div>
            </div>
            
            <div class="product-specs" v-if="selectedProductDetail.specs">
              <h4>商品规格</h4>
              <ul>
                <li v-for="spec in selectedProductDetail.specs" :key="spec">{{ spec }}</li>
              </ul>
            </div>
          </div>
        </div>
        
        <div class="modal-footer">
          <div class="exchange-info">
            <span class="current-points">当前积分：{{ userPoints }}</span>
            <span class="after-exchange">兑换后剩余：{{ userPoints - selectedProductDetail.points }}</span>
          </div>
          
          <div class="modal-actions">
            <button class="btn btn-secondary" @click="closeModal">取消</button>
            <button 
              class="btn btn-primary"
              :disabled="!canExchange(selectedProductDetail)"
              @click="exchangeProduct(selectedProductDetail)"
            >
              {{ getExchangeButtonText(selectedProductDetail) }}
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Exchange Success Modal -->
    <div v-if="showSuccessModal" class="modal-overlay" @click="closeSuccessModal">
      <div class="success-modal" @click.stop>
        <div class="success-icon">🎉</div>
        <h2>兑换成功！</h2>
        <p>恭喜您成功兑换了「{{ exchangedProduct?.name }}」</p>
        <p class="success-points">消耗积分：{{ exchangedProduct?.points }}</p>
        <p class="success-remaining">剩余积分：{{ userPoints }}</p>
        <button class="btn btn-primary" @click="closeSuccessModal">确定</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, defineEmits } from 'vue'

const emit = defineEmits(['show-login'])

// 用户积分
const userPoints = ref(1250)

// 分类数据
const categories = ref([
  { id: 'all', name: '全部', icon: '🛍️' },
  { id: 'electronics', name: '数码产品', icon: '📱' },
  { id: 'books', name: '图书文具', icon: '📚' },
  { id: 'lifestyle', name: '生活用品', icon: '🏠' },
  { id: 'food', name: '美食特产', icon: '🍎' },
  { id: 'charity', name: '公益捐赠', icon: '❤️' }
])

// 选中的分类
const selectedCategory = ref('all')
const selectedProductDetail = ref(null)
const showSuccessModal = ref(false)
const exchangedProduct = ref(null)

// 商品数据
const products = ref([
  {
    id: 1,
    name: '蓝牙无线耳机',
    description: '高品质音效，长续航，运动防汗',
    fullDescription: '这款蓝牙无线耳机采用最新的蓝牙5.0技术，提供稳定的连接和高品质的音效。配备主动降噪功能，让您在嘈杂环境中也能享受纯净的音乐。超长续航时间，单次充电可使用8小时，配合充电盒总续航达到32小时。',
    image: 'https://plus.unsplash.com/premium_photo-1680346529370-c940bd7536ad?q=80&w=1074&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D',
    category: 'electronics',
    points: 800,
    stock: 15,
    rating: 4.8,
    reviews: 156,
    isHot: true,
    shipping: '包邮到家',
    specs: ['蓝牙5.0连接', '主动降噪', '8小时续航', 'IPX4防水', '快速充电']
  },
  {
    id: 2,
    name: '精装励志图书套装',
    description: '经典励志书籍，提升个人修养',
    fullDescription: '这套励志图书包含了多本经典的个人成长和励志类书籍，涵盖时间管理、情商提升、领导力培养等多个方面。每本书都经过精心挑选，适合不同年龄段的读者阅读，是提升自我、充实精神世界的绝佳选择。',
    image: 'https://plus.unsplash.com/premium_photo-1734709479958-dbee57558557?q=80&w=687&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D',
    category: 'books',
    points: 300,
    stock: 25,
    rating: 4.6,
    reviews: 89,
    isHot: false,
    shipping: '包邮到家',
    specs: ['精装版本', '5本套装', '经典励志', '适合收藏', '正版授权']
  },
  {
    id: 3,
    name: '环保购物袋套装',
    description: '可重复使用，环保材质，多种尺寸',
    fullDescription: '这套环保购物袋采用优质的可降解材料制作，结实耐用，可重复使用数百次。套装包含大中小三种尺寸，满足不同购物需求。精美的设计和环保理念的结合，让您在购物的同时为环保事业贡献力量。',
    image: 'https://images.unsplash.com/photo-1657593322459-a0cb1ff1902d?q=80&w=1170&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D',
    category: 'lifestyle',
    points: 150,
    stock: 50,
    rating: 4.4,
    reviews: 234,
    isHot: false,
    shipping: '包邮到家',
    specs: ['环保材质', '3个装', '可折叠', '承重15kg', '多色可选']
  },
  {
    id: 4,
    name: '有机蜂蜜礼盒',
    description: '纯天然有机蜂蜜，营养丰富',
    fullDescription: '来自无污染山区的纯天然有机蜂蜜，通过有机认证，不含任何添加剂。蜂蜜色泽金黄，口感醇厚，富含多种维生素和矿物质。精美的礼盒包装，是送礼和自用的理想选择。',
    image: 'https://images.unsplash.com/photo-1587468362560-e64c0ede4c5a?q=80&w=687&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D',
    category: 'food',
    points: 400,
    stock: 8,
    rating: 4.9,
    reviews: 67,
    isHot: true,
    shipping: '冷链配送',
    specs: ['有机认证', '500g装', '山花蜜', '无添加', '礼盒包装']
  },
  {
    id: 5,
    name: '智能手环',
    description: '健康监测，运动追踪，消息提醒',
    fullDescription: '这款智能手环集成了多种健康监测功能，包括心率监测、睡眠分析、运动追踪等。支持多种运动模式，防水设计适合游泳使用。长续航设计，一次充电可使用7天。配合手机APP，让您更好地了解自己的健康状况。',
    image: 'https://images.unsplash.com/photo-1586484716673-9ba792bf9dbe?q=80&w=686&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D',
    category: 'electronics',
    points: 600,
    stock: 20,
    rating: 4.5,
    reviews: 198,
    isHot: false,
    shipping: '包邮到家',
    specs: ['心率监测', '防水设计', '7天续航', '多运动模式', 'APP同步']
  },
  {
    id: 6,
    name: '为山区儿童捐赠文具',
    description: '为贫困山区的孩子们送去学习用品',
    fullDescription: '您的积分将转化为实际的文具用品，包括铅笔、橡皮、笔记本、彩色笔等，直接送到贫困山区的孩子们手中。每一份捐赠都会有详细的反馈报告，让您了解您的爱心如何帮助到需要的孩子们。',
    image: 'https://plus.unsplash.com/premium_photo-1664110691177-f9c861df3703?q=80&w=687&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D',
    category: 'charity',
    points: 200,
    stock: 999,
    rating: 5.0,
    reviews: 456,
    isHot: true,
    shipping: '直接捐赠',
    specs: ['直接捐赠', '反馈报告', '帮助儿童', '学习用品', '爱心传递']
  },
  {
    id: 7,
    name: '便携式充电宝',
    description: '大容量，快充技术，多设备兼容',
    fullDescription: '20000mAh大容量充电宝，支持快充技术，可同时为多个设备充电。内置智能芯片，自动识别设备类型并提供最适合的充电功率。轻薄设计，方便携带，是出行必备的电子产品。',
    image: 'https://plus.unsplash.com/premium_photo-1679423807759-afb3c145c535?q=80&w=1170&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D',
    category: 'electronics',
    points: 500,
    stock: 12,
    rating: 4.7,
    reviews: 123,
    isHot: false,
    shipping: '包邮到家',
    specs: ['20000mAh', '快充技术', '多口输出', '智能识别', '轻薄便携']
  },
  {
    id: 8,
    name: '创意笔记本套装',
    description: '精美设计，优质纸张，激发创作灵感',
    fullDescription: '这套创意笔记本包含3本不同主题的笔记本，采用优质纸张制作，书写流畅不渗透。精美的封面设计和内页排版，为您的创作和记录提供完美的载体。适合学生、职场人士和创作者使用。',
    image: 'https://plus.unsplash.com/premium_photo-1685136482508-ff00024d187a?q=80&w=1170&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D',
    category: 'books',
    points: 180,
    stock: 35,
    rating: 4.3,
    reviews: 78,
    isHot: false,
    shipping: '包邮到家',
    specs: ['3本套装', '优质纸张', '创意设计', '多种规格', '精装封面']
  }
])

// 筛选后的商品
const filteredProducts = computed(() => {
  if (selectedCategory.value === 'all') {
    return products.value
  }
  return products.value.filter(product => product.category === selectedCategory.value)
})

// 方法
const canExchange = (product) => {
  return userPoints.value >= product.points && product.stock > 0
}

const getExchangeButtonText = (product) => {
  if (product.stock === 0) {
    return '已售罄'
  }
  if (userPoints.value < product.points) {
    return '积分不足'
  }
  return '立即兑换'
}

const selectProduct = (product) => {
  selectedProductDetail.value = product
}

const closeModal = () => {
  selectedProductDetail.value = null
}

const closeSuccessModal = () => {
  showSuccessModal.value = false
  exchangedProduct.value = null
}

const exchangeProduct = (product) => {
  if (!canExchange(product)) return
  
  // 这里应该检查用户登录状态
  // 如果未登录，显示登录弹窗
  // emit('show-login')
  
  // 模拟兑换成功
  userPoints.value -= product.points
  product.stock -= 1
  exchangedProduct.value = product
  showSuccessModal.value = true
  closeModal()
}
</script>

<style scoped>
.store-page {
  min-height: 100vh;
  background: #f8f9fa;
}

.page-header {
  background: linear-gradient(135deg, #ff6b6b 0%, #ee5a24 100%);
  color: white;
  text-align: center;
  padding: 60px 20px 40px;
}

.page-header h1 {
  font-size: 2.5rem;
  margin-bottom: 10px;
}

.page-header p {
  font-size: 1.1rem;
  opacity: 0.9;
  margin-bottom: 30px;
}

.user-points {
  display: flex;
  justify-content: center;
}

.points-display {
  background: rgba(255,255,255,0.2);
  padding: 15px 30px;
  border-radius: 25px;
  display: flex;
  align-items: center;
  gap: 10px;
  backdrop-filter: blur(10px);
}

.points-icon {
  font-size: 1.5rem;
}

.points-text {
  font-size: 1rem;
}

.points-value {
  font-size: 1.5rem;
  font-weight: 700;
}

.categories-section {
  background: white;
  padding: 20px;
  box-shadow: 0 2px 10px rgba(0,0,0,0.1);
}

.categories-container {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  gap: 15px;
  justify-content: center;
  flex-wrap: wrap;
}

.category-btn {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 5px;
  padding: 15px 20px;
  border: 2px solid #e1e5e9;
  border-radius: 15px;
  background: white;
  cursor: pointer;
  transition: all 0.3s ease;
  min-width: 100px;
}

.category-btn:hover {
  border-color: #ff6b6b;
  transform: translateY(-2px);
}

.category-btn.active {
  border-color: #ff6b6b;
  background: #ff6b6b;
  color: white;
}

.category-icon {
  font-size: 1.5rem;
}

.category-name {
  font-size: 0.9rem;
  font-weight: 600;
}

.products-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 40px 20px;
}

.products-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 25px;
}

.product-card {
  background: white;
  border-radius: 15px;
  overflow: hidden;
  box-shadow: 0 5px 15px rgba(0,0,0,0.08);
  transition: all 0.3s ease;
  cursor: pointer;
}

.product-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 25px rgba(0,0,0,0.15);
}

.product-image {
  position: relative;
  height: 200px;
  overflow: hidden;
}

.product-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.product-card:hover .product-image img {
  transform: scale(1.05);
}

.stock-warning {
  position: absolute;
  top: 10px;
  left: 10px;
  background: #ff4757;
  color: white;
  padding: 5px 10px;
  border-radius: 12px;
  font-size: 0.8rem;
  font-weight: 600;
}

.hot-badge {
  position: absolute;
  top: 10px;
  right: 10px;
  background: #ff6b6b;
  color: white;
  padding: 5px 10px;
  border-radius: 12px;
  font-size: 0.8rem;
  font-weight: 600;
}

.product-content {
  padding: 20px;
}

.product-name {
  font-size: 1.1rem;
  font-weight: 600;
  margin-bottom: 8px;
  color: #333;
}

.product-description {
  color: #666;
  font-size: 0.9rem;
  line-height: 1.4;
  margin-bottom: 15px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.product-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.product-points {
  display: flex;
  align-items: baseline;
  gap: 5px;
}

.points-required {
  font-size: 1.3rem;
  font-weight: 700;
  color: #ff6b6b;
}

.points-unit {
  font-size: 0.9rem;
  color: #666;
}

.product-stock {
  font-size: 0.8rem;
  color: #666;
}

.product-rating {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 15px;
}

.stars {
  display: flex;
  gap: 2px;
}

.star {
  font-size: 0.8rem;
  opacity: 0.3;
}

.star.filled {
  opacity: 1;
}

.rating-text {
  font-size: 0.8rem;
  color: #666;
}

.btn-exchange {
  width: 100%;
  padding: 12px;
  border: none;
  border-radius: 8px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  background: #ff6b6b;
  color: white;
}

.btn-exchange:hover:not(:disabled) {
  background: #ff5252;
}

.btn-exchange:disabled {
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

/* Modal Styles */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0,0,0,0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  padding: 20px;
}

.modal-content {
  background: white;
  border-radius: 15px;
  max-width: 600px;
  width: 100%;
  max-height: 90vh;
  overflow-y: auto;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  border-bottom: 1px solid #eee;
}

.modal-header h2 {
  margin: 0;
  color: #333;
}

.close-btn {
  background: none;
  border: none;
  font-size: 1.5rem;
  cursor: pointer;
  color: #666;
  padding: 5px;
}

.modal-body {
  padding: 20px;
}

.product-detail-image {
  text-align: center;
  margin-bottom: 20px;
}

.product-detail-image img {
  max-width: 100%;
  height: 250px;
  object-fit: cover;
  border-radius: 10px;
}

.detail-description {
  line-height: 1.6;
  color: #333;
  margin-bottom: 20px;
}

.detail-info {
  background: #f8f9fa;
  padding: 15px;
  border-radius: 10px;
  margin-bottom: 20px;
}

.info-row {
  margin-bottom: 10px;
  color: #333;
}

.info-row:last-child {
  margin-bottom: 0;
}

.rating-stars {
  margin: 0 5px;
}

.product-specs {
  border-top: 1px solid #eee;
  padding-top: 15px;
}

.product-specs h4 {
  margin-bottom: 10px;
  color: #333;
}

.product-specs ul {
  list-style: none;
  padding: 0;
}

.product-specs li {
  padding: 5px 0;
  color: #666;
  border-bottom: 1px solid #f0f0f0;
}

.product-specs li:last-child {
  border-bottom: none;
}

.modal-footer {
  padding: 20px;
  border-top: 1px solid #eee;
}

.exchange-info {
  display: flex;
  justify-content: space-between;
  margin-bottom: 15px;
  font-size: 0.9rem;
  color: #666;
}

.modal-actions {
  display: flex;
  gap: 10px;
  justify-content: flex-end;
}

.btn {
  padding: 10px 20px;
  border: none;
  border-radius: 8px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.btn-secondary {
  background: #6c757d;
  color: white;
}

.btn-secondary:hover {
  background: #5a6268;
}

.btn-primary {
  background: #ff6b6b;
  color: white;
}

.btn-primary:hover:not(:disabled) {
  background: #ff5252;
}

.btn-primary:disabled {
  background: #ccc;
  cursor: not-allowed;
}

/* Success Modal */
.success-modal {
  background: white;
  border-radius: 15px;
  padding: 40px;
  text-align: center;
  max-width: 400px;
  width: 100%;
}

.success-icon {
  font-size: 4rem;
  margin-bottom: 20px;
}

.success-modal h2 {
  color: #333;
  margin-bottom: 15px;
}

.success-modal p {
  color: #666;
  margin-bottom: 10px;
}

.success-points {
  color: #ff6b6b;
  font-weight: 600;
}

.success-remaining {
  color: #4CAF50;
  font-weight: 600;
  margin-bottom: 25px !important;
}

/* Responsive */
@media (max-width: 768px) {
  .products-grid {
    grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  }
  
  .categories-container {
    justify-content: flex-start;
    overflow-x: auto;
    padding-bottom: 10px;
  }
  
  .category-btn {
    flex-shrink: 0;
    min-width: 80px;
  }
  
  .modal-content {
    margin: 10px;
    max-height: calc(100vh - 20px);
  }
  
  .exchange-info {
    flex-direction: column;
    gap: 5px;
  }
  
  .modal-actions {
    flex-direction: column;
  }
}
</style>