<template>
  <div class="coming-soon">
    <div class="content">
      <div class="icon">🔜</div>
      <h1>敬请期待</h1>
      <p>我们正在努力开发更多实用功能</p>
      <p class="sub">体育模块、娱乐模块即将上线...</p>
      <el-button type="primary" @click="$router.push('/')">返回首页</el-button>
    </div>
  </div>
</template>

<style scoped>
.coming-soon {
  min-height: calc(100vh - 200px);
  display: flex;
  align-items: center;
  justify-content: center;
  text-align: center;
}

.content {
  padding: 60px;
  background: white;
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
}

.icon {
  font-size: 80px;
  margin-bottom: 30px;
}

h1 {
  margin-bottom: 15px;
  color: #333;
}

p {
  color: #666;
  margin-bottom: 10px;
}

.sub {
  color: #999;
  font-size: 14px;
  margin-bottom: 30px;
}
</style>
