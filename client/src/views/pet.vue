<template>
  <!-- 宠物管理页 -->
  <div class="page-container fade-in-up">
    <!-- 页面标题 -->
    <div class="page-header">
      <div>
        <div class="page-title">宠物管理</div>
        <div class="page-desc">维护平台上的宠物领养信息，支持图片上传、编辑与上下架</div>
      </div>
      <el-button type="primary" :icon="Plus" @click="handleAdd">新增宠物</el-button>
    </div>

    <!-- 搜索栏 -->
    <div class="search-bar">
      <el-form :inline="true" @submit.prevent>
        <el-form-item label="关键字">
          <el-input v-model="query.keyword" placeholder="昵称 / 描述" clearable style="width: 180px" @keyup.enter="handleSearch" />
        </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="query.categoryId" placeholder="全部分类" clearable style="width: 140px">
            <el-option v-for="c in categories" :key="c.id" :label="c.name" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="城市">
          <el-input v-model="query.city" placeholder="所在城市" clearable style="width: 130px" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="query.status" placeholder="全部状态" clearable style="width: 130px">
            <el-option label="待领养" :value="1" />
            <el-option label="已领养" :value="2" />
            <el-option label="已下架" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleSearch">查询</el-button>
          <el-button :icon="Refresh" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 宠物表格 -->
    <div class="table-card">
      <el-table v-loading="loading" :data="list" style="width: 100%">
        <el-table-column label="宠物" min-width="170">
          <template #default="{ row }">
            <div class="pet-cell">
              <img class="pet-thumb" :src="row.coverImg" :alt="row.name" />
              <div class="pet-info">
                <div class="pet-name">{{ row.name }}</div>
                <div class="pet-sub">{{ row.categoryName }} · {{ row.gender }}</div>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="年龄" prop="age" width="90" />
        <el-table-column label="城市" prop="city" width="90" />
        <el-table-column label="费用" width="100">
          <template #default="{ row }">
            <span :class="row.price > 0 ? 'price' : 'price-free'">
              {{ row.price > 0 ? '¥' + row.price : '免费' }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="statusType(row.status)" size="small" effect="light">
              {{ statusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="浏览" prop="viewCount" width="80" align="center" />
        <el-table-column label="发布人" prop="userNickname" width="100" />
        <el-table-column label="发布时间" prop="createTime" width="160" />
        <el-table-column label="操作" width="210" align="center" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link :icon="Edit" @click="handleEdit(row)">编辑</el-button>
            <el-button type="warning" link :icon="Switch" @click="handleStatus(row)">
              {{ row.status === 0 ? '上架' : '下架' }}
            </el-button>
            <el-button type="danger" link :icon="Delete" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
        <template #empty>
          <div class="empty-box">暂无宠物数据</div>
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
    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑宠物信息' : '新增宠物信息'" width="720px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-row :gutter="14">
          <el-col :span="12">
            <el-form-item label="宠物昵称" prop="name">
              <el-input v-model="form.name" placeholder="请输入宠物昵称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="所属分类" prop="categoryId">
              <el-select v-model="form.categoryId" placeholder="请选择分类" style="width: 100%">
                <el-option v-for="c in categories" :key="c.id" :label="c.icon + ' ' + c.name" :value="c.id" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="年龄" prop="age">
              <el-input v-model="form.age" placeholder="如：3个月 / 2岁" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="性别" prop="gender">
              <el-radio-group v-model="form.gender">
                <el-radio-button label="弟弟" />
                <el-radio-button label="妹妹" />
                <el-radio-button label="未知" />
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="所在城市" prop="city">
              <el-input v-model="form.city" placeholder="请输入城市" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="领养费用" prop="price">
              <el-input-number v-model="form.price" :min="0" :precision="2" :step="50" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="健康状况" prop="health">
              <el-input v-model="form.health" placeholder="如：已接种疫苗" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-select v-model="form.status" style="width: 100%">
                <el-option label="待领养" :value="1" />
                <el-option label="已领养" :value="2" />
                <el-option label="已下架" :value="0" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="详细介绍" prop="description">
              <el-input v-model="form.description" type="textarea" :rows="3" placeholder="介绍一下它的性格、习惯与故事" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="防疫情况">
              <el-checkbox v-model="form.vaccine" :true-value="1" :false-value="0" label="已接种疫苗" />
              <el-checkbox v-model="form.sterilized" :true-value="1" :false-value="0" label="已绝育" />
              <el-checkbox v-model="form.deworm" :true-value="1" :false-value="0" label="已驱虫" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="宠物图片">
              <!-- 图片上传组件 -->
              <el-upload
                v-model:file-list="imageList"
                list-type="picture-card"
                :limit="5"
                :auto-upload="false"
                :on-change="handleFileChange"
                :on-remove="handleFileRemove"
                :on-preview="handlePreview"
              >
                <el-icon><Plus /></el-icon>
              </el-upload>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 图片预览 -->
    <el-dialog v-model="previewVisible" width="60%">
      <img :src="previewUrl" style="width: 100%; border-radius: 12px" />
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search, Refresh, Edit, Delete, Switch } from '@element-plus/icons-vue'
import { pagePet, addPet, updatePet, updatePetStatus, deletePet } from '@/api/pet'
import { listCategory } from '@/api/category'
import { uploadFile } from '@/api/file'

// 表格加载状态
const loading = ref(false)
// 提交状态
const submitting = ref(false)
// 宠物列表
const list = ref([])
// 总记录数
const total = ref(0)
// 分类列表
const categories = ref([])
// 查询参数
const query = reactive({
  page: 1,
  pageSize: 10,
  keyword: '',
  categoryId: '',
  city: '',
  status: ''
})
// 弹窗显示状态
const dialogVisible = ref(false)
// 表单引用
const formRef = ref(null)
// 图片列表
const imageList = ref([])
// 图片预览
const previewVisible = ref(false)
// 预览地址
const previewUrl = ref('')

// 表单数据
const form = reactive({
  id: null,
  name: '',
  categoryId: null,
  age: '',
  gender: '未知',
  city: '',
  price: 0,
  health: '',
  description: '',
  coverImg: '',
  vaccine: 0,
  sterilized: 0,
  deworm: 0,
  status: 1
})

// 表单校验规则
const rules = {
  name: [{ required: true, message: '请输入宠物昵称', trigger: 'blur' }],
  categoryId: [{ required: true, message: '请选择所属分类', trigger: 'change' }]
}

/**
 * 加载宠物列表
 */
async function loadData() {
  loading.value = true
  try {
    const res = await pagePet(query)
    list.value = res.data.records || []
    total.value = res.data.total || 0
  } catch (e) {
    // 错误已由拦截器处理
  } finally {
    loading.value = false
  }
}

/**
 * 加载分类列表
 */
async function loadCategories() {
  const res = await listCategory()
  categories.value = res.data || []
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
  Object.assign(query, { page: 1, keyword: '', categoryId: '', city: '', status: '' })
  loadData()
}

/**
 * 打开新增弹窗
 */
function handleAdd() {
  Object.assign(form, {
    id: null,
    name: '',
    categoryId: null,
    age: '',
    gender: '未知',
    city: '',
    price: 0,
    health: '',
    description: '',
    coverImg: '',
    vaccine: 0,
    sterilized: 0,
    deworm: 0,
    status: 1
  })
  imageList.value = []
  dialogVisible.value = true
}

/**
 * 打开编辑弹窗
 *
 * @param {Object} row 当前行数据
 */
function handleEdit(row) {
  Object.assign(form, row)
  // 回显已上传图片
  imageList.value = []
  const urls = row.coverImg ? [row.coverImg] : []
  urls.forEach((url) => {
    imageList.value.push({ name: url.split('/').pop(), url, status: 'success', uid: Date.now() + Math.random() })
  })
  dialogVisible.value = true
}

/**
 * 选择图片后的处理（暂存文件，待提交时统一上传）
 *
 * @param {Object} file 文件对象
 */
function handleFileChange(file) {
  imageList.value = imageList.value.map((item) => {
    if (item.uid === file.uid && file.raw) {
      return { ...item, raw: file.raw }
    }
    return item
  })
}

/**
 * 移除图片
 *
 * @param {Object} file 文件对象
 */
function handleFileRemove(file) {
  imageList.value = imageList.value.filter((item) => item.uid !== file.uid)
}

/**
 * 预览图片
 *
 * @param {Object} file 文件对象
 */
function handlePreview(file) {
  previewUrl.value = file.url
  previewVisible.value = true
}

/**
 * 提交表单（先上传图片，再保存数据）
 */
async function handleSubmit() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) {
    return
  }
  submitting.value = true
  try {
    // 上传新选择的图片
    const urls = []
    for (const item of imageList.value) {
      if (item.raw) {
        const fd = new FormData()
        fd.append('file', item.raw)
        const uploadRes = await uploadFile(fd)
        urls.push(uploadRes.data.url)
      } else if (item.url) {
        urls.push(item.url)
      }
    }
    form.coverImg = urls.length ? urls[0] : ''
    const payload = { ...form, images: urls }
    if (form.id) {
      await updatePet(form.id, payload)
      ElMessage.success('修改成功')
    } else {
      await addPet(payload)
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
 * 上下架宠物
 *
 * @param {Object} row 当前行数据
 */
async function handleStatus(row) {
  try {
    await ElMessageBox.confirm(`确定要${row.status === 0 ? '上架' : '下架'}宠物「${row.name}」吗？`, '提示', { type: 'warning' })
    await updatePetStatus(row.id)
    ElMessage.success('操作成功')
    loadData()
  } catch (e) {
    // 用户取消
  }
}

/**
 * 删除宠物
 *
 * @param {Object} row 当前行数据
 */
async function handleDelete(row) {
  try {
    await ElMessageBox.confirm(`确定要删除宠物「${row.name}」吗？此操作不可恢复。`, '危险操作', {
      type: 'error',
      confirmButtonText: '确定删除',
      confirmButtonClass: 'el-button--danger'
    })
    await deletePet(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch (e) {
    // 用户取消
  }
}

/**
 * 根据状态返回标签类型
 *
 * @param {Number} status 状态值
 * @returns {String} 标签类型
 */
function statusType(status) {
  return { 0: 'info', 1: 'success', 2: 'warning' }[status] || 'info'
}

/**
 * 根据状态返回中文名称
 *
 * @param {Number} status 状态值
 * @returns {string} 状态名称
 */
function statusText(status) {
  return { 0: '已下架', 1: '待领养', 2: '已领养' }[status] || '未知'
}

onMounted(() => {
  loadData()
  loadCategories()
})
</script>

<style scoped lang="scss">
.pet-cell {
  display: flex;
  align-items: center;
  gap: 10px;
}

.pet-info {
  line-height: 1.4;
}

.pet-name {
  font-size: 14px;
  font-weight: 600;
  color: var(--text-main);
}

.pet-sub {
  font-size: 12px;
  color: var(--text-sub);
}

.price {
  color: #ff7a45;
  font-weight: 600;
}

.price-free {
  color: #52c41a;
  font-weight: 600;
}
</style>
