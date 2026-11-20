<template>
  <!-- 用户管理页 -->
  <div class="page-container fade-in-up">
    <!-- 页面标题 -->
    <div class="page-header">
      <div>
        <div class="page-title">用户管理</div>
        <div class="page-desc">管理系统用户与小程序用户，支持新增、编辑、启用禁用与删除</div>
      </div>
      <el-button type="primary" :icon="Plus" @click="handleAdd">新增用户</el-button>
    </div>

    <!-- 搜索栏 -->
    <div class="search-bar">
      <el-form :inline="true" @submit.prevent>
        <el-form-item label="关键字">
          <el-input
            v-model="query.keyword"
            placeholder="账号 / 昵称"
            clearable
            style="width: 200px"
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item label="角色">
          <el-select v-model="query.role" placeholder="全部" clearable style="width: 140px">
            <el-option label="管理员" value="admin" />
            <el-option label="普通用户" value="user" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleSearch">查询</el-button>
          <el-button :icon="Refresh" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 用户表格 -->
    <div class="table-card">
      <el-table v-loading="loading" :data="list" style="width: 100%">
        <el-table-column label="ID" prop="id" width="60" align="center" />
        <el-table-column label="账号" prop="username" min-width="110" />
        <el-table-column label="昵称" prop="nickname" min-width="100" />
        <el-table-column label="电话" prop="phone" min-width="120" />
        <el-table-column label="城市" prop="city" width="90" />
        <el-table-column label="角色" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.role === 'admin' ? 'danger' : 'primary'" size="small" effect="light">
              {{ row.role === 'admin' ? '管理员' : '普通用户' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="90" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small" effect="light">
              {{ row.status === 1 ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" prop="createTime" width="160" />
        <el-table-column label="操作" width="200" align="center" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link :icon="Edit" @click="handleEdit(row)">编辑</el-button>
            <el-button type="warning" link :icon="Switch" @click="handleStatus(row)">
              {{ row.status === 1 ? '禁用' : '启用' }}
            </el-button>
            <el-button type="danger" link :icon="Delete" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
        <template #empty>
          <div class="empty-box">暂无用户数据</div>
        </template>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-box">
        <el-pagination
          v-model:current-page="query.page"
          v-model:page-size="query.pageSize"
          :page-sizes="[10, 20, 50]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="loadData"
          @current-change="loadData"
        />
      </div>
    </div>

    <!-- 新增 / 编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑用户' : '新增用户'" width="520px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="登录账号" prop="username">
          <el-input v-model="form.username" placeholder="请输入登录账号" :disabled="!!form.id" />
        </el-form-item>
        <el-form-item label="登录密码" prop="password">
          <el-input v-model="form.password" placeholder="不填则默认为 123456" />
        </el-form-item>
        <el-form-item label="用户昵称" prop="nickname">
          <el-input v-model="form.nickname" placeholder="请输入昵称" />
        </el-form-item>
        <el-form-item label="联系电话" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入联系电话" />
        </el-form-item>
        <el-form-item label="所在城市" prop="city">
          <el-input v-model="form.city" placeholder="请输入所在城市" />
        </el-form-item>
        <el-form-item label="角色" prop="role">
          <el-radio-group v-model="form.role">
            <el-radio label="user">普通用户</el-radio>
            <el-radio label="admin">管理员</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-switch v-model="form.status" :active-value="1" :inactive-value="0" />
        </el-form-item>
        <el-form-item label="个人简介" prop="intro">
          <el-input v-model="form.intro" type="textarea" :rows="3" placeholder="简单介绍一下自己" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search, Refresh, Edit, Delete, Switch } from '@element-plus/icons-vue'
import { pageUser, addUser, updateUser, updateUserStatus, deleteUser } from '@/api/user'

// 表格加载状态
const loading = ref(false)
// 提交状态
const submitting = ref(false)
// 用户列表
const list = ref([])
// 总记录数
const total = ref(0)
// 查询参数
const query = reactive({
  page: 1,
  pageSize: 10,
  keyword: '',
  role: ''
})
// 弹窗显示状态
const dialogVisible = ref(false)
// 表单引用
const formRef = ref(null)

// 表单数据
const form = reactive({
  id: null,
  username: '',
  password: '',
  nickname: '',
  phone: '',
  city: '',
  role: 'user',
  status: 1,
  intro: ''
})

// 表单校验规则
const rules = {
  username: [{ required: true, message: '请输入登录账号', trigger: 'blur' }],
  nickname: [{ required: true, message: '请输入用户昵称', trigger: 'blur' }]
}

/**
 * 加载用户列表
 */
async function loadData() {
  loading.value = true
  try {
    const res = await pageUser(query)
    list.value = res.data.records || []
    total.value = res.data.total || 0
  } catch (e) {
    // 错误已由拦截器处理
  } finally {
    loading.value = false
  }
}

/**
 * 执行查询
 */
function handleSearch() {
  query.page = 1
  loadData()
}

/**
 * 重置查询条件
 */
function handleReset() {
  query.keyword = ''
  query.role = ''
  query.page = 1
  loadData()
}

/**
 * 打开新增弹窗
 */
function handleAdd() {
  Object.assign(form, {
    id: null,
    username: '',
    password: '',
    nickname: '',
    phone: '',
    city: '',
    role: 'user',
    status: 1,
    intro: ''
  })
  dialogVisible.value = true
}

/**
 * 打开编辑弹窗
 *
 * @param {Object} row 当前行数据
 */
function handleEdit(row) {
  Object.assign(form, row)
  form.password = ''
  dialogVisible.value = true
}

/**
 * 提交表单
 */
async function handleSubmit() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) {
    return
  }
  submitting.value = true
  try {
    if (form.id) {
      await updateUser(form.id, form)
      ElMessage.success('修改成功')
    } else {
      await addUser(form)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    loadData()
  } catch (e) {
    // 错误已由拦截器处理
  } finally {
    submitting.value = false
  }
}

/**
 * 修改用户状态
 *
 * @param {Object} row 当前行数据
 */
async function handleStatus(row) {
  try {
    await ElMessageBox.confirm(
      `确定要${row.status === 1 ? '禁用' : '启用'}用户「${row.nickname || row.username}」吗？`,
      '提示',
      { type: 'warning' }
    )
    await updateUserStatus(row.id)
    ElMessage.success('操作成功')
    loadData()
  } catch (e) {
    // 用户取消
  }
}

/**
 * 删除用户
 *
 * @param {Object} row 当前行数据
 */
async function handleDelete(row) {
  try {
    await ElMessageBox.confirm(
      `确定要删除用户「${row.nickname || row.username}」吗？此操作不可恢复。`,
      '危险操作',
      { type: 'error', confirmButtonText: '确定删除', confirmButtonClass: 'el-button--danger' }
    )
    await deleteUser(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch (e) {
    // 用户取消
  }
}

onMounted(loadData)
</script>
