<template>
  <!-- 个人中心 -->
  <div class="page-container fade-in-up">
    <el-row :gutter="16">
      <!-- 左侧用户信息卡片 -->
      <el-col :xs="24" :md="8">
        <div class="profile-card pet-card">
          <div class="profile-bg"></div>
          <div class="profile-body">
            <el-avatar :size="86" class="profile-avatar">{{ avatarText }}</el-avatar>
            <h3 class="profile-name">{{ user.nickname || user.username }}</h3>
            <div class="profile-role">
              <el-tag :type="user.role === 'admin' ? 'danger' : 'primary'" size="small" effect="light">
                {{ user.role === 'admin' ? '管理员' : '普通用户' }}
              </el-tag>
            </div>
            <ul class="profile-list">
              <li><el-icon><User /></el-icon><span>登录账号</span><b>{{ user.username }}</b></li>
              <li><el-icon><Iphone /></el-icon><span>联系电话</span><b>{{ user.phone || '未填写' }}</b></li>
              <li><el-icon><Location /></el-icon><span>所在城市</span><b>{{ user.city || '未填写' }}</b></li>
              <li><el-icon><Calendar /></el-icon><span>注册时间</span><b>{{ formatTime(user.createTime) }}</b></li>
            </ul>
            <div class="profile-intro">{{ user.intro || '这位用户还没有填写个人简介。' }}</div>
          </div>
        </div>
      </el-col>

      <!-- 右侧信息卡 -->
      <el-col :xs="24" :md="16">
        <div class="table-card">
          <div class="page-header">
            <div>
              <div class="page-title">账号信息</div>
              <div class="page-desc">修改昵称、电话、城市等个人资料，密码默认为 123456</div>
            </div>
          </div>
          <el-form ref="formRef" :model="form" :rules="rules" label-width="100px" class="profile-form">
            <el-form-item label="登录账号">
              <el-input v-model="form.username" disabled />
            </el-form-item>
            <el-form-item label="用户昵称" prop="nickname">
              <el-input v-model="form.nickname" placeholder="请输入用户昵称" />
            </el-form-item>
            <el-form-item label="联系电话" prop="phone">
              <el-input v-model="form.phone" placeholder="请输入联系电话" />
            </el-form-item>
            <el-form-item label="所在城市" prop="city">
              <el-input v-model="form.city" placeholder="请输入所在城市" />
            </el-form-item>
            <el-form-item label="个人简介" prop="intro">
              <el-input v-model="form.intro" type="textarea" :rows="4" placeholder="介绍一下自己吧" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" :loading="submitting" @click="handleSave">保存修改</el-button>
            </el-form-item>
          </el-form>
        </div>

        <!-- 系统信息 -->
        <div class="table-card system-card">
          <div class="page-header">
            <div>
              <div class="page-title">关于系统</div>
              <div class="page-desc">Java1234 微信小程序宠物领养系统 · 本科毕业设计</div>
            </div>
          </div>
          <el-row :gutter="16">
            <el-col :span="8">
              <div class="tech-item">
                <div class="tech-label">后端框架</div>
                <div class="tech-value">Spring Boot 4</div>
              </div>
            </el-col>
            <el-col :span="8">
              <div class="tech-item">
                <div class="tech-label">持久层</div>
                <div class="tech-value">MyBatis-Plus</div>
              </div>
            </el-col>
            <el-col :span="8">
              <div class="tech-item">
                <div class="tech-label">数据库</div>
                <div class="tech-value">MySQL 8 (3308)</div>
              </div>
            </el-col>
            <el-col :span="8">
              <div class="tech-item">
                <div class="tech-label">前端框架</div>
                <div class="tech-value">Vue 3</div>
              </div>
            </el-col>
            <el-col :span="8">
              <div class="tech-item">
                <div class="tech-label">UI 组件</div>
                <div class="tech-value">Element Plus</div>
              </div>
            </el-col>
            <el-col :span="8">
              <div class="tech-item">
                <div class="tech-label">图表库</div>
                <div class="tech-value">ECharts 5</div>
              </div>
            </el-col>
          </el-row>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getUser, setLoginInfo } from '@/utils/auth'
import { getUserInfo } from '@/api/auth'
import { updateUser } from '@/api/user'

// 当前登录用户
const user = reactive(getUser() || {})
// 提交状态
const submitting = ref(false)
// 表单引用
const formRef = ref(null)

// 表单数据
const form = reactive({
  username: '',
  nickname: '',
  phone: '',
  city: '',
  intro: ''
})

// 表单校验规则
const rules = {
  nickname: [{ required: true, message: '请输入用户昵称', trigger: 'blur' }]
}

// 头像文字
const avatarText = computed(() => {
  const name = user.nickname || user.username || 'U'
  return name.charAt(0).toUpperCase()
})

/**
 * 格式化时间
 *
 * @param {String} time 时间字符串
 * @returns {string} 格式化后的时间
 */
function formatTime(time) {
  if (!time) {
    return '未知'
  }
  return time.replace('T', ' ').substring(0, 16)
}

/**
 * 保存个人资料
 */
async function handleSave() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) {
    return
  }
  submitting.value = true
  try {
    await updateUser(user.id, { nickname: form.nickname, phone: form.phone, city: form.city, intro: form.intro })
    // 刷新本地缓存
    const res = await getUserInfo()
    Object.assign(user, res.data)
    setLoginInfo({ token: localStorage.getItem('token'), user: res.data })
    ElMessage.success('保存成功')
  } catch (e) {
    // 错误已由拦截器处理
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  form.username = user.username || ''
  form.nickname = user.nickname || ''
  form.phone = user.phone || ''
  form.city = user.city || ''
  form.intro = user.intro || ''
})
</script>

<style scoped lang="scss">
/* 用户信息卡片 */
.profile-card {
  overflow: hidden;
  margin-bottom: 16px;
}

.profile-bg {
  height: 110px;
  background: linear-gradient(120deg, #ff7a45 0%, #ff9c6e 45%, #36cfc9 100%);
}

.profile-body {
  padding: 0 22px 24px;
  text-align: center;
}

.profile-avatar {
  margin-top: -44px;
  border: 4px solid #fff;
  background: linear-gradient(135deg, #ff7a45, #36cfc9);
  color: #fff;
  font-weight: 700;
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.14);
}

.profile-name {
  font-size: 19px;
  color: var(--text-main);
  margin: 12px 0 8px;
}

.profile-role {
  margin-bottom: 18px;
}

.profile-list {
  list-style: none;
  text-align: left;

  li {
    display: flex;
    align-items: center;
    gap: 9px;
    font-size: 13px;
    color: var(--text-sub);
    padding: 9px 0;
    border-bottom: 1px dashed #f0f2f7;

    b {
      margin-left: auto;
      color: var(--text-main);
      font-weight: 600;
    }
  }
}

.profile-intro {
  margin-top: 16px;
  padding: 12px;
  border-radius: 10px;
  background: #f7f9fc;
  font-size: 13px;
  color: #6b7688;
  line-height: 1.8;
  text-align: left;
}

/* 表单卡片 */
.profile-form {
  max-width: 520px;
}

/* 系统信息 */
.system-card {
  margin-top: 16px;
}

.tech-item {
  padding: 14px;
  border-radius: 12px;
  background: linear-gradient(135deg, #f7f9fc, #eef6ff);
  margin-bottom: 14px;
  border-left: 3px solid var(--primary);
}

.tech-label {
  font-size: 12px;
  color: var(--text-sub);
}

.tech-value {
  font-size: 15px;
  font-weight: 700;
  color: var(--text-main);
  margin-top: 4px;
}
</style>
