<template>
  <!-- 登录页：渐变背景 + 居中玻璃卡片 -->
  <div class="login-page">
    <!-- 背景装饰光斑 -->
    <div class="blob blob-1"></div>
    <div class="blob blob-2"></div>
    <div class="blob blob-3"></div>

    <div class="login-card">
      <!-- 左侧品牌区 -->
      <div class="brand-side">
        <div class="brand-logo">🐾</div>
        <h1 class="brand-title">Java1234</h1>
        <p class="brand-sub">微信小程序宠物领养系统</p>
        <ul class="brand-points">
          <li><span>✔</span> 宠物信息在线展示</li>
          <li><span>✔</span> 一键提交领养申请</li>
          <li><span>✔</span> 后台数据可视化统计</li>
        </ul>
      </div>

      <!-- 右侧登录表单 -->
      <div class="form-side">
        <h2 class="form-title">欢迎回来</h2>
        <p class="form-desc">请输入账号密码登录管理后台</p>

        <el-form ref="formRef" :model="form" :rules="rules" size="large" @keyup.enter="handleLogin">
          <el-form-item prop="username">
            <el-input v-model="form.username" placeholder="请输入登录账号" clearable>
              <template #prefix>
                <el-icon><User /></el-icon>
              </template>
            </el-input>
          </el-form-item>

          <el-form-item prop="password">
            <el-input v-model="form.password" type="password" placeholder="请输入登录密码" show-password clearable>
              <template #prefix>
                <el-icon><Lock /></el-icon>
              </template>
            </el-input>
          </el-form-item>

          <el-form-item>
            <el-button type="primary" class="login-btn" :loading="loading" @click="handleLogin">
              {{ loading ? '登录中...' : '登 录' }}
            </el-button>
          </el-form-item>
        </el-form>

        <!-- 测试账号提示 -->
        <div class="tip-box">
          <el-icon><InfoFilled /></el-icon>
          <span>测试账号：admin / 123456（管理员）</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { login } from '@/api/auth'
import { setLoginInfo } from '@/utils/auth'

// 路由对象
const router = useRouter()
// 路由参数
const route = useRoute()
// 表单引用
const formRef = ref(null)
// 登录加载状态
const loading = ref(false)

// 登录表单数据
const form = reactive({
  username: 'admin',
  password: '123456'
})

// 表单校验规则
const rules = {
  username: [{ required: true, message: '请输入登录账号', trigger: 'blur' }],
  password: [{ required: true, message: '请输入登录密码', trigger: 'blur' }]
}

/**
 * 执行登录
 */
async function handleLogin() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) {
    return
  }
  loading.value = true
  try {
    const res = await login(form)
    // 保存登录信息
    setLoginInfo(res.data)
    ElMessage.success('登录成功，欢迎回来！')
    // 跳转到首页或来源页面
    router.push(route.query.redirect || '/home')
  } catch (e) {
    // 错误提示已由拦截器统一处理
  } finally {
    loading.value = false
  }
}
</script>

<style scoped lang="scss">
.login-page {
  position: relative;
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 45%, #ff7a45 100%);
}

/* 背景装饰光斑 */
.blob {
  position: absolute;
  border-radius: 50%;
  filter: blur(70px);
  opacity: 0.55;
  animation: float 8s ease-in-out infinite;

  &.blob-1 {
    width: 320px;
    height: 320px;
    background: #ff9a6e;
    top: -80px;
    left: -60px;
  }

  &.blob-2 {
    width: 260px;
    height: 260px;
    background: #36cfc9;
    bottom: -70px;
    right: -40px;
    animation-delay: 2s;
  }

  &.blob-3 {
    width: 200px;
    height: 200px;
    background: #ffd166;
    top: 45%;
    right: 22%;
    animation-delay: 4s;
  }
}

@keyframes float {
  0%,
  100% {
    transform: translate(0, 0);
  }
  50% {
    transform: translate(24px, -26px);
  }
}

.login-card {
  position: relative;
  z-index: 2;
  width: 860px;
  max-width: 94vw;
  display: flex;
  border-radius: 22px;
  overflow: hidden;
  box-shadow: 0 26px 60px rgba(0, 0, 0, 0.28);
  background: #fff;
  animation: cardIn 0.6s ease;
}

@keyframes cardIn {
  from {
    opacity: 0;
    transform: translateY(30px) scale(0.97);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

/* 左侧品牌区 */
.brand-side {
  flex: 1;
  padding: 52px 40px;
  background: linear-gradient(160deg, #2b3a67 0%, #1e2a4a 100%);
  color: #fff;
  position: relative;

  &::after {
    content: '';
    position: absolute;
    right: -60px;
    bottom: -60px;
    width: 220px;
    height: 220px;
    border-radius: 50%;
    background: rgba(255, 122, 69, 0.28);
  }
}

.brand-logo {
  width: 64px;
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 34px;
  border-radius: 20px;
  background: linear-gradient(135deg, var(--primary), var(--secondary));
  box-shadow: 0 10px 24px rgba(255, 122, 69, 0.45);
  margin-bottom: 22px;
}

.brand-title {
  font-size: 34px;
  letter-spacing: 3px;
  margin-bottom: 6px;
}

.brand-sub {
  font-size: 15px;
  color: #a9b6d4;
  letter-spacing: 1px;
  margin-bottom: 30px;
}

.brand-points {
  list-style: none;

  li {
    font-size: 14px;
    color: #cfd6e4;
    margin-bottom: 14px;
    display: flex;
    align-items: center;
    gap: 9px;

    span {
      width: 20px;
      height: 20px;
      display: inline-flex;
      align-items: center;
      justify-content: center;
      border-radius: 50%;
      background: rgba(54, 207, 201, 0.22);
      color: #36cfc9;
      font-size: 12px;
    }
  }
}

/* 右侧表单区 */
.form-side {
  width: 400px;
  padding: 52px 42px;
  flex-shrink: 0;
}

.form-title {
  font-size: 25px;
  color: var(--text-main);
  margin-bottom: 6px;
}

.form-desc {
  font-size: 13px;
  color: var(--text-sub);
  margin-bottom: 30px;
}

.login-btn {
  width: 100%;
  height: 46px;
  font-size: 16px;
  font-weight: 600;
  letter-spacing: 4px;
  border: none;
  border-radius: 12px;
  background: linear-gradient(90deg, var(--primary), var(--secondary));
  transition: all 0.25s;

  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 10px 22px rgba(255, 122, 69, 0.4);
  }
}

.tip-box {
  margin-top: 22px;
  padding: 10px 14px;
  border-radius: 10px;
  background: #f6f8fc;
  border: 1px dashed #d8dfeb;
  color: #7b869a;
  font-size: 12px;
  display: flex;
  align-items: center;
  gap: 7px;
}
</style>
