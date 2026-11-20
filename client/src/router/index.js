import { createRouter, createWebHashHistory } from 'vue-router'
import { getToken } from '@/utils/auth'

/**
 * 路由配置
 *
 * <p>采用哈希模式，方便部署到任意静态服务器，无需额外的重写规则。</p>
 */
const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login.vue'),
    meta: { title: '登录', public: true }
  },
  {
    path: '/',
    component: () => import('@/layout/index.vue'),
    redirect: '/home',
    children: [
      {
        path: 'home',
        name: 'Home',
        component: () => import('@/views/home.vue'),
        meta: { title: '数据首页', icon: 'DataLine' }
      },
      {
        path: 'pet',
        name: 'Pet',
        component: () => import('@/views/pet.vue'),
        meta: { title: '宠物管理', icon: 'Star' }
      },
      {
        path: 'category',
        name: 'Category',
        component: () => import('@/views/category.vue'),
        meta: { title: '分类管理', icon: 'Menu' }
      },
      {
        path: 'apply',
        name: 'Apply',
        component: () => import('@/views/apply.vue'),
        meta: { title: '领养审核', icon: 'DocumentChecked' }
      },
      {
        path: 'user',
        name: 'User',
        component: () => import('@/views/user.vue'),
        meta: { title: '用户管理', icon: 'User' }
      },
      {
        path: 'notice',
        name: 'Notice',
        component: () => import('@/views/notice.vue'),
        meta: { title: '公告管理', icon: 'Bell' }
      },
      {
        path: 'comment',
        name: 'Comment',
        component: () => import('@/views/comment.vue'),
        meta: { title: '留言管理', icon: 'ChatDotRound' }
      },
      {
        path: 'profile',
        name: 'Profile',
        component: () => import('@/views/profile.vue'),
        meta: { title: '个人中心', icon: 'Setting' }
      }
    ]
  },
  {
    path: '/:pathMatch(.*)*',
    redirect: '/home'
  }
]

const router = createRouter({
  history: createWebHashHistory(),
  routes
})

// 全局前置守卫：未登录跳转到登录页
router.beforeEach((to, from, next) => {
  if (to.meta && to.meta.public) {
    next()
    return
  }
  if (!getToken()) {
    next({ path: '/login', query: { redirect: to.fullPath } })
    return
  }
  // 设置页面标题
  document.title = (to.meta && to.meta.title ? to.meta.title + ' - ' : '') + 'Java1234 宠物领养系统'
  next()
})

export default router
