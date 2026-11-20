<template>
  <!-- 管理后台整体布局：左侧渐变菜单 + 右侧内容区 -->
  <div class="layout">
    <!-- 左侧菜单栏 -->
    <aside class="sidebar" :class="{ collapse: isCollapse }">
      <!-- 系统 Logo -->
      <div class="logo">
        <div class="logo-icon">🐾</div>
        <div class="logo-text" v-show="!isCollapse">
          <div class="logo-title">Java1234</div>
          <div class="logo-sub">宠物领养系统</div>
        </div>
      </div>

      <!-- 菜单列表 -->
      <el-menu
        class="menu"
        :default-active="activeMenu"
        :collapse="isCollapse"
        :collapse-transition="false"
        router
        background-color="transparent"
        text-color="#cfd6e4"
        active-text-color="#ffffff"
      >
        <el-menu-item v-for="item in menus" :key="item.path" :index="item.path">
          <el-icon><component :is="item.icon" /></el-icon>
          <template #title>{{ item.title }}</template>
        </el-menu-item>
      </el-menu>
    </aside>

    <!-- 右侧主区域 -->
    <div class="main-area">
      <!-- 顶部导航栏 -->
      <header class="topbar">
        <div class="topbar-left">
          <!-- 折叠按钮 -->
          <el-icon class="collapse-btn" @click="isCollapse = !isCollapse">
            <component :is="isCollapse ? 'Expand' : 'Fold'" />
          </el-icon>
          <!-- 面包屑 -->
          <el-breadcrumb separator="/">
            <el-breadcrumb-item>管理后台</el-breadcrumb-item>
            <el-breadcrumb-item>{{ currentTitle }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>

        <div class="topbar-right">
          <!-- 天气风格的装饰标签 -->
          <el-tag class="top-tag" type="warning" effect="light" round size="small">
            🐾 领养代替购买
          </el-tag>

          <!-- 用户下拉菜单 -->
          <el-dropdown @command="handleCommand">
            <div class="user-info">
              <el-avatar :size="36" class="user-avatar">{{ avatarText }}</el-avatar>
              <div class="user-meta">
                <span class="user-name">{{ user.nickname || user.username }}</span>
                <span class="user-role">{{ user.role === 'admin' ? '管理员' : '普通用户' }}</span>
              </div>
              <el-icon><ArrowDown /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">
                  <el-icon><Setting /></el-icon>个人中心
                </el-dropdown-item>
                <el-dropdown-item command="logout" divided>
                  <el-icon><SwitchButton /></el-icon>退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </header>

      <!-- 内容区域 -->
      <section class="content">
        <router-view v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </section>
    </div>
  </div>
</template>

<script setup>
import { computed, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessageBox, ElMessage } from 'element-plus'
import { getUser, clearLoginInfo } from '@/utils/auth'

// 路由对象
const route = useRoute()
// 路由跳转对象
const router = useRouter()
// 菜单折叠状态
const isCollapse = ref(false)
// 当前登录用户
const user = ref(getUser() || {})

// 菜单配置
const menus = [
  { path: '/home', title: '数据首页', icon: 'DataLine' },
  { path: '/pet', title: '宠物管理', icon: 'Star' },
  { path: '/category', title: '分类管理', icon: 'Menu' },
  { path: '/apply', title: '领养审核', icon: 'DocumentChecked' },
  { path: '/user', title: '用户管理', icon: 'User' },
  { path: '/notice', title: '公告管理', icon: 'Bell' },
  { path: '/comment', title: '留言管理', icon: 'ChatDotRound' },
  { path: '/profile', title: '个人中心', icon: 'Setting' }
]

// 当前高亮的菜单
const activeMenu = computed(() => route.path)
// 当前页面标题
const currentTitle = computed(() => (route.meta && route.meta.title) || '首页')
// 头像文字
const avatarText = computed(() => {
  const name = user.value.nickname || user.value.username || 'U'
  return name.charAt(0).toUpperCase()
})

/**
 * 处理下拉菜单命令
 *
 * @param {string} command 命令名
 */
function handleCommand(command) {
  if (command === 'profile') {
    router.push('/profile')
  } else if (command === 'logout') {
    // 确认退出
    ElMessageBox.confirm('确定要退出登录吗？', '提示', {
      confirmButtonText: '确定退出',
      cancelButtonText: '再想想',
      type: 'warning'
    }).then(() => {
      clearLoginInfo()
      ElMessage.success('已退出登录')
      router.push('/login')
    }).catch(() => {})
  }
}
</script>

<style scoped lang="scss">
.layout {
  display: flex;
  height: 100vh;
  overflow: hidden;
}

/* ---------- 左侧菜单 ---------- */
.sidebar {
  width: 220px;
  flex-shrink: 0;
  background: linear-gradient(165deg, #2b3a67 0%, #1e2a4a 55%, #17213a 100%);
  transition: width 0.28s ease;
  display: flex;
  flex-direction: column;
  box-shadow: 4px 0 20px rgba(0, 0, 0, 0.12);

  &.collapse {
    width: 66px;
  }
}

.logo {
  display: flex;
  align-items: center;
  gap: 10px;
  height: 62px;
  padding: 0 16px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);

  .logo-icon {
    width: 38px;
    height: 38px;
    flex-shrink: 0;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 21px;
    border-radius: 12px;
    background: linear-gradient(135deg, var(--primary), var(--secondary));
    box-shadow: 0 4px 12px rgba(255, 122, 69, 0.4);
  }

  .logo-title {
    color: #fff;
    font-size: 16px;
    font-weight: 700;
    letter-spacing: 1px;
  }

  .logo-sub {
    color: #8b97b5;
    font-size: 11px;
    margin-top: 2px;
  }
}

.menu {
  flex: 1;
  border-right: none;
  padding-top: 10px;

  :deep(.el-menu-item) {
    height: 48px;
    line-height: 48px;
    margin: 4px 10px;
    border-radius: 10px;

    &:hover {
      background: rgba(255, 255, 255, 0.08) !important;
    }

    &.is-active {
      background: linear-gradient(90deg, var(--primary), var(--secondary)) !important;
      box-shadow: 0 6px 16px rgba(255, 122, 69, 0.35);
    }
  }
}

/* ---------- 右侧区域 ---------- */
.main-area {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-width: 0;
}

.topbar {
  height: 62px;
  flex-shrink: 0;
  background: #fff;
  box-shadow: 0 2px 12px rgba(31, 45, 61, 0.06);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 22px;
  z-index: 10;
}

.topbar-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.collapse-btn {
  font-size: 20px;
  cursor: pointer;
  color: #5a6478;
  transition: color 0.2s;

  &:hover {
    color: var(--primary);
  }
}

.topbar-right {
  display: flex;
  align-items: center;
  gap: 18px;
}

.top-tag {
  font-weight: 600;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  outline: none;
}

.user-avatar {
  background: linear-gradient(135deg, var(--primary), var(--secondary));
  color: #fff;
  font-weight: 700;
}

.user-meta {
  display: flex;
  flex-direction: column;
  line-height: 1.35;
}

.user-name {
  font-size: 14px;
  font-weight: 600;
  color: var(--text-main);
}

.user-role {
  font-size: 11px;
  color: var(--text-sub);
}

.content {
  flex: 1;
  overflow-y: auto;
  background: var(--bg-page);
}

/* 路由切换动画 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.22s ease, transform 0.22s ease;
}

.fade-enter-from {
  opacity: 0;
  transform: translateX(18px);
}

.fade-leave-to {
  opacity: 0;
  transform: translateX(-18px);
}
</style>
