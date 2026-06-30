import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import request from '../utils/request'
import { ElMessage } from 'element-plus'

export const useCartStore = defineStore('cart', () => {
  const cartList = ref([])
  const cartCount = ref(0)

  const totalAmount = computed(() => {
    return cartList.value
      .filter(item => item.checked === 1)
      .reduce((sum, item) => sum + item.subtotal, 0)
  })

  const checkedCount = computed(() => {
    return cartList.value.filter(item => item.checked === 1).length
  })

  const isAllChecked = computed(() => {
    return cartList.value.length > 0 && cartList.value.every(item => item.checked === 1)
  })

  // 获取购物车列表
  async function fetchCartList() {
    const res = await request.get('/api/cart/list')
    cartList.value = res.data
  }

  // 获取购物车数量
  async function fetchCartCount() {
    const res = await request.get('/api/cart/count')
    cartCount.value = res.data
  }

  // 添加到购物车
  async function addToCart(productId, quantity = 1) {
    await request.post('/api/cart/add', { productId, quantity })
    await fetchCartCount()
    ElMessage.success('已添加到购物车')
  }

  // 更新数量
  async function updateQuantity(cartId, quantity) {
    await request.put('/api/cart/quantity', { cartId, quantity })
    await fetchCartList()
  }

  // 更新选中状态
  async function updateChecked(cartId, checked) {
    await request.put('/api/cart/checked', { cartId, checked })
    await fetchCartList()
  }

  // 全选/取消全选
  async function updateAllChecked(checked) {
    await request.put('/api/cart/all-checked', { checked })
    await fetchCartList()
  }

  // 删除购物车商品
  async function deleteCartItem(cartId) {
    await request.delete(`/api/cart/${cartId}`)
    await fetchCartList()
    await fetchCartCount()
    ElMessage.success('已删除')
  }

  // 清空购物车
  async function clearCart() {
    await request.delete('/api/cart/clear')
    cartList.value = []
    cartCount.value = 0
    ElMessage.success('购物车已清空')
  }

  return {
    cartList,
    cartCount,
    totalAmount,
    checkedCount,
    isAllChecked,
    fetchCartList,
    fetchCartCount,
    addToCart,
    updateQuantity,
    updateChecked,
    updateAllChecked,
    deleteCartItem,
    clearCart
  }
})
