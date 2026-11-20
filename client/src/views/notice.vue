<template>
  <!-- 公告管理页 -->
  <div class="page-container fade-in-up">
    <!-- 页面标题 -->
    <div class="page-header">
      <div>
        <div class="page-title">公告管理</div>
        <div class="page-desc">发布与维护平台公告、领养活动与养宠攻略，展示在小程序首页</div>
      </div>
      <el-button type="primary" :icon="Plus" @click="handleAdd">新增公告</el-button>
    </div>

    <!-- 搜索栏 -->
    <div class="search-bar">
      <el-form :inline="true" @submit.prevent>
        <el-form-item label="标题关键字">
          <el-input v-model="query.keyword" placeholder="请输入标题关键字" clearable style="width: 200px" @keyup.enter="handleSearch" />
        </el-form-item>
        <el-form-item label="类型">
          <el-select v-model="query.type" placeholder="全部类型" clearable style="width: 140px">
            <el-option label="通知" value="通知" />
            <el-option label="活动" value="活动" />
            <el-option label="攻略" value="攻略" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleSearch">查询</el-button>
          <el-button :icon="Refresh" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 公告表格 -->
    <div class="table-card">
      <el-table v-loading="loading" :data="list" style="width: 100%">
        <el-table-column label="ID" prop="id" width="60" align="center" />
        <el-table-column label="封面" width="90" align="center">
          <template #default="{ row }">
            <img v-if="row.cover" class="pet-thumb" :src="row.cover" :alt="row.title" />
            <span v-else class="no-cover">无</span>
          </template>
        </el-table-column>
        <el-table-column label="标题" prop="title" min-width="220" show-overflow-tooltip />
        <el-table-column label="类型" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="typeTag(row.type)" size="small" effect="light">{{ row.type }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="阅读量" prop="viewCount" width="90" align="center" />
        <el-table-column label="状态" width="90" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small" effect="light">
              {{ row.status === 1 ? '展示中' : '已隐藏' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="发布时间" prop="createTime" width="160" />
        <el-table-column label="操作" width="180" align="center" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link :icon="Edit" @click="handleEdit(row)">编辑</el-button>
            <el-button type="info" link :icon="View" @click="handlePreview(row)">预览</el-button>
            <el-button type="danger" link :icon="Delete" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
        <template #empty>
          <div class="empty-box">暂无公告数据</div>
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
    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑公告' : '新增公告'" width="720px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="公告标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入公告标题" />
        </el-form-item>
        <el-form-item label="公告类型" prop="type">
          <el-radio-group v-model="form.type">
            <el-radio-button label="通知" />
            <el-radio-button label="活动" />
            <el-radio-button label="攻略" />
          </el-radio-group>
        </el-form-item>
        <el-form-item label="封面图片" prop="cover">
          <el-upload
            v-model:file-list="coverList"
            list-type="picture-card"
            :limit="1"
            :auto-upload="false"
            :on-change="handleFileChange"
            :on-remove="handleFileRemove"
            :on-preview="handlePreviewImage"
          >
            <el-icon><Plus /></el-icon>
          </el-upload>
        </el-form-item>
        <el-form-item label="公告正文" prop="content">
          <el-input v-model="form.content" type="textarea" :rows="8" placeholder="请输入公告正文内容" />
        </el-form-item>
        <el-form-item label="是否展示" prop="status">
          <el-switch v-model="form.status" :active-value="1" :inactive-value="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 公告预览弹窗 -->
    <el-dialog v-model="previewVisible" title="公告预览" width="640px">
      <article v-if="current" class="notice-preview">
        <h2>{{ current.title }}</h2>
        <div class="preview-meta">
          <el-tag size="small" :type="typeTag(current.type)">{{ current.type }}</el-tag>
          <span>{{ current.createTime }}</span>
          <span>{{ current.viewCount }} 次阅读</span>
        </div>
        <img v-if="current.cover" :src="current.cover" class="preview-cover" />
        <div class="preview-content">{{ current.content }}</div>
      </article>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search, Refresh, Edit, Delete, View } from '@element-plus/icons-vue'
import { pageNotice, addNotice, updateNotice, deleteNotice } from '@/api/notice'
import { uploadFile } from '@/api/file'

// 表格加载状态
const loading = ref(false)
// 提交状态
const submitting = ref(false)
// 公告列表
const list = ref([])
// 总记录数
const total = ref(0)
// 弹窗显示状态
const dialogVisible = ref(false)
// 预览弹窗显示状态
const previewVisible = ref(false)
// 当前预览的公告
const current = ref(null)
// 表单引用
const formRef = ref(null)
// 封面图片列表
const coverList = ref([])

// 查询参数
const query = reactive({
  page: 1,
  pageSize: 10,
  keyword: '',
  type: ''
})

// 表单数据
const form = reactive({
  id: null,
  title: '',
  content: '',
  cover: '',
  type: '通知',
  status: 1
})

// 表单校验规则
const rules = {
  title: [{ required: true, message: '请输入公告标题', trigger: 'blur' }],
  content: [{ required: true, message: '请输入公告正文', trigger: 'blur' }]
}

/**
 * 加载公告列表
 */
async function loadData() {
  loading.value = true
  try {
    const res = await pageNotice(query)
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
  Object.assign(query, { page: 1, keyword: '', type: '' })
  loadData()
}

/**
 * 打开新增弹窗
 */
function handleAdd() {
  Object.assign(form, { id: null, title: '', content: '', cover: '', type: '通知', status: 1 })
  coverList.value = []
  dialogVisible.value = true
}

/**
 * 打开编辑弹窗
 *
 * @param {Object} row 当前公告
 */
function handleEdit(row) {
  Object.assign(form, row)
  coverList.value = []
  if (row.cover) {
    coverList.value.push({ name: row.cover.split('/').pop(), url: row.cover, status: 'success', uid: Date.now() })
  }
  dialogVisible.value = true
}

/**
 * 选择封面图片
 *
 * @param {Object} file 文件对象
 */
function handleFileChange(file) {
  coverList.value = coverList.value.map((item) => {
    if (item.uid === file.uid && file.raw) {
      return { ...item, raw: file.raw }
    }
    return item
  })
}

/**
 * 移除封面图片
 *
 * @param {Object} file 文件对象
 */
function handleFileRemove(file) {
  coverList.value = coverList.value.filter((item) => item.uid !== file.uid)
}

/**
 * 预览封面图片
 *
 * @param {Object} file 文件对象
 */
function handlePreviewImage(file) {
  window.open(file.url, '_blank')
}

/**
 * 预览公告
 *
 * @param {Object} row 当前公告
 */
function handlePreview(row) {
  current.value = row
  previewVisible.value = true
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
    // 上传封面图片
    if (coverList.value.length && coverList.value[0].raw) {
      const fd = new FormData()
      fd.append('file', coverList.value[0].raw)
      const uploadRes = await uploadFile(fd)
      form.cover = uploadRes.data.url
    } else if (coverList.value.length) {
      form.cover = coverList.value[0].url
    }
    if (form.id) {
      await updateNotice(form.id, form)
      ElMessage.success('修改成功')
    } else {
      await addNotice(form)
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
 * 删除公告
 *
 * @param {Object} row 当前公告
 */
async function handleDelete(row) {
  try {
    await ElMessageBox.confirm(`确定要删除公告「${row.title}」吗？`, '提示', { type: 'warning' })
    await deleteNotice(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch (e) {
    // 用户取消
  }
}

/**
 * 根据类型返回标签样式
 *
 * @param {String} type 公告类型
 * @returns {String} 标签类型
 */
function typeTag(type) {
  return { 通知: 'primary', 活动: 'danger', 攻略: 'success' }[type] || 'info'
}

onMounted(loadData)
</script>

<style scoped lang="scss">
.no-cover {
  font-size: 12px;
  color: var(--text-sub);
}

.notice-preview h2 {
  font-size: 22px;
  color: var(--text-main);
  margin-bottom: 10px;
}

.preview-meta {
  display: flex;
  align-items: center;
  gap: 14px;
  font-size: 13px;
  color: var(--text-sub);
  padding-bottom: 14px;
  border-bottom: 1px solid #f0f2f7;
  margin-bottom: 14px;
}

.preview-cover {
  width: 100%;
  border-radius: 12px;
  margin-bottom: 14px;
}

.preview-content {
  font-size: 14px;
  line-height: 2;
  color: #4c5666;
  white-space: pre-wrap;
}
</style>
