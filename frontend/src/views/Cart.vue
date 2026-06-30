<script setup>
import { onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useCartStore } from '../stores/cart'
import { ElMessageBox, ElMessage } from 'element-plus'

const router = useRouter()
const cartStore = useCartStore()

onMounted(async () => {
  await cartStore.fetchCartList()
})

const handleQuantityChange = async (cartId, quantity) => {
  if (quantity < 1) {
    await cartStore.deleteCartItem(cartId)
    return
  }
  await cartStore.updateQuantity(cartId, quantity)
}

const handleCheckedChange = async (cartId, checked) => {
  await cartStore.updateChecked(cartId, checked ? 1 : 0)
}

const handleAllCheckedChange = async (checked) => {
  await cartStore.updateAllChecked(checked ? 1 : 0)
}

const handleDelete = async (cartId) => {
  await ElMessageBox.confirm('确定要删除该商品吗？', '提示', {
    type: 'warning'
  })
  await cartStore.deleteCartItem(cartId)
}

const handleClearCart = async () => {
  await ElMessageBox.confirm('确定要清空购物车吗？', '提示', {
    type: 'warning'
  })
  await cartStore.clearCart()
}

const goToCheckout = () => {
  if (cartStore.checkedCount === 0) {
    ElMessage.warning('请选择要结算的商品')
    return
  }
  router.push('/order/confirm')
}

const goToProduct = (productId) => {
  router.push(`/product/${productId}`)
}
</script>

<template>
  <div class="cart-page">
    <div class="cart-header">
      <h2>我的购物车</h2>
      <span class="cart-count">共 {{ cartStore.cartList.length }} 件商品</span>
    </div>

    <div v-if="cartStore.cartList.length === 0" class="empty-cart">
      <el-empty description="购物车是空的">
        <el-button type="primary" @click="router.push('/')">去逛逛</el-button>
      </el-empty>
    </div>

    <div v-else class="cart-content">
      <!-- 购物车列表 -->
      <div class="cart-list">
        <div class="cart-header-row">
          <el-checkbox
            v-model="cartStore.isAllChecked"
            @change="handleAllCheckedChange"
          >
            全选
          </el-checkbox>
          <span class="header-product">商品信息</span>
          <span class="header-price">单价</span>
          <span class="header-quantity">数量</span>
          <span class="header-subtotal">小计</span>
          <span class="header-action">操作</span>
        </div>

        <div
          v-for="item in cartStore.cartList"
          :key="item.id"
          class="cart-item"
        >
          <el-checkbox
            :model-value="item.checked === 1"
            @change="(val) => handleCheckedChange(item.id, val)"
          />

          <div class="item-product" @click="goToProduct(item.productId)">
            <img :src="item.productImage" :alt="item.productName" />
            <span class="product-name">{{ item.productName }}</span>
          </div>

          <div class="item-price">
            <span>¥{{ item.price }}</span>
          </div>

          <div class="item-quantity">
            <el-input-number
              :model-value="item.quantity"
              :min="1"
              :max="item.stock"
              @change="(val) => handleQuantityChange(item.id, val)"
              size="small"
            />
          </div>

          <div class="item-subtotal">
            <span>¥{{ item.subtotal.toFixed(2) }}</span>
          </div>

          <div class="item-action">
            <el-button
              type="danger"
              text
              @click="handleDelete(item.id)"
            >
              删除
            </el-button>
          </div>
        </div>
      </div>

      <!-- 结算栏 -->
      <div class="cart-footer">
        <div class="footer-left">
          <el-checkbox
            v-model="cartStore.isAllChecked"
            @change="handleAllCheckedChange"
          >
            全选
          </el-checkbox>
          <el-button text @click="handleClearCart">清空购物车</el-button>
        </div>

        <div class="footer-right">
          <div class="selected-info">
            已选择 <span>{{ cartStore.checkedCount }}</span> 件商品
          </div>
          <div class="total-info">
            合计：<span class="total-price">¥{{ cartStore.totalAmount.toFixed(2) }}</span>
          </div>
          <el-button
            type="primary"
            size="large"
            :disabled="cartStore.checkedCount === 0"
            @click="goToCheckout"
          >
            去结算
          </el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.cart-page {
  max-width: 1200px;
  margin: 0 auto;
}

.cart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.cart-header h2 {
  margin: 0;
}

.cart-count {
  color: #999;
}

.empty-cart {
  background: white;
  border-radius: 8px;
  padding: 60px;
  text-align: center;
}

.cart-content {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.cart-header-row {
  display: flex;
  align-items: center;
  padding: 15px 20px;
  background: #f5f5f5;
  border-radius: 8px 8px 0 0;
  font-weight: 500;
}

.cart-header-row > *:first-child {
  width: 60px;
}

.header-product {
  flex: 1;
  margin-left: 20px;
}

.header-price,
.header-quantity,
.header-subtotal,
.header-action {
  width: 120px;
  text-align: center;
}

.cart-item {
  display: flex;
  align-items: center;
  padding: 20px;
  border-bottom: 1px solid #eee;
}

.cart-item > *:first-child {
  width: 60px;
}

.item-product {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 15px;
  margin-left: 20px;
  cursor: pointer;
}

.item-product img {
  width: 80px;
  height: 80px;
  object-fit: cover;
  border-radius: 4px;
}

.product-name {
  font-size: 14px;
}

.item-price,
.item-quantity,
.item-subtotal,
.item-action {
  width: 120px;
  text-align: center;
}

.item-subtotal span {
  color: #f56c6c;
  font-weight: bold;
}

.cart-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  border-top: 1px solid #eee;
  background: #fff;
  border-radius: 0 0 8px 8px;
}

.footer-left {
  display: flex;
  align-items: center;
  gap: 20px;
}

.footer-right {
  display: flex;
  align-items: center;
  gap: 30px;
}

.selected-info {
  color: #999;
}

.selected-info span {
  color: #667eea;
}

.total-info {
  font-size: 16px;
}

.total-price {
  color: #f56c6c;
  font-size: 24px;
  font-weight: bold;
}
</style>
