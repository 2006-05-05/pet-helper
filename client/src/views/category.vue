<template>
  <!-- 分类管理页 -->
  <div class="page-container fade-in-up">
    <!-- 页面标题 -->
    <div class="page-header">
      <div>
        <div class="page-title">分类管理</div>
        <div class="page-desc">维护宠物的分类信息，用于小程序的分类筛选与首页快捷入口</div>
      </div>
      <el-button type="primary" :icon="Plus" @click="handleAdd">新增分类</el-button>
    </div>

    <!-- 分类卡片列表 -->
    <el-row :gutter="16">
      <el-col v-for="item in list" :key="item.id" :xs="12" :sm="8" :md="6">
        <div class="category-card pet-card">
          <div class="cat-icon">{{ item.icon }}</div>
          <div class="cat-body">
            <div class="cat-name">{{ item.name }}</div>
            <div class="cat-desc">{{ item.description || '暂无描述' }}</div>
            <div class="cat-sort">排序：{{ item.sort }}</div>
          </div>
          <div class="cat-actions">
            <el-button type="primary" link :icon="Edit" @click="handleEdit(item)">编辑</el-button>
            <el-button type="danger" link :icon="Delete" @click="handleDelete(item)">删除</el-button>
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- 新增 / 编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑分类' : '新增分类'" width="460px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="分类名称" prop="name">
          <el-input v-model="form.name" placeholder="如：狗狗" />
        </el-form-item>
        <el-form-item label="分类图标" prop="icon">
          <el-input v-model="form.icon" placeholder="如：🐶" />
          <div class="icon-tips">
            <span class="icon-label">常用图标：</span>
            <el-tag
              v-for="ic in iconOptions"
              :key="ic"
              class="icon-tag"
              :effect="form.icon === ic ? 'dark' : 'light'"
              @click="form.icon = ic"
            >
              {{ ic }}
            </el-tag>
          </div>
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input-number v-model="form.sort" :min="0" :max="999" style="width: 100%" />
        </el-form-item>
        <el-form-item label="分类描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="简单描述该分类" />
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
import { Plus, Edit, Delete } from '@element-plus/icons-vue'
import { listCategory, addCategory, updateCategory, deleteCategory } from '@/api/category'

// 分类列表
const list = ref([])
// 弹窗显示状态
const dialogVisible = ref(false)
// 提交状态
const submitting = ref(false)
// 表单引用
const formRef = ref(null)

// 常用图标选项
const iconOptions = ['🐶', '🐱', '🐰', '🐾', '🐹', '🐦', '🦜', '🐢', '🦊', '🐨']

// 表单数据
const form = reactive({
  id: null,
  name: '',
  icon: '🐾',
  description: '',
  sort: 0
})

// 表单校验规则
const rules = {
  name: [{ required: true, message: '请输入分类名称', trigger: 'blur' }],
  icon: [{ required: true, message: '请输入分类图标', trigger: 'blur' }]
}

/**
 * 加载分类列表
 */
async function loadData() {
  const res = await listCategory()
  list.value = res.data || []
}

/**
 * 打开新增弹窗
 */
function handleAdd() {
  Object.assign(form, { id: null, name: '', icon: '🐾', description: '', sort: list.value.length + 1 })
  dialogVisible.value = true
}

/**
 * 打开编辑弹窗
 *
 * @param {Object} row 当前分类
 */
function handleEdit(row) {
  Object.assign(form, row)
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
      await updateCategory(form.id, form)
      ElMessage.success('修改成功')
    } else {
      await addCategory(form)
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
 * 删除分类
 *
 * @param {Object} row 当前分类
 */
async function handleDelete(row) {
  try {
    await ElMessageBox.confirm(`确定要删除分类「${row.name}」吗？`, '提示', { type: 'warning' })
    await deleteCategory(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch (e) {
    // 用户取消
  }
}

onMounted(loadData)
</script>

<style scoped lang="scss">
.category-card {
  padding: 20px;
  margin-bottom: 16px;
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
}

.cat-icon {
  width: 64px;
  height: 64px;
  border-radius: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 32px;
  background: linear-gradient(135deg, #fff2e8, #e6fffb);
  margin-bottom: 12px;
}

.cat-name {
  font-size: 16px;
  font-weight: 700;
  color: var(--text-main);
}

.cat-desc {
  font-size: 12px;
  color: var(--text-sub);
  margin-top: 6px;
  line-height: 1.6;
  min-height: 38px;
}

.cat-sort {
  font-size: 11px;
  color: #b6bdc9;
  margin-top: 4px;
}

.cat-actions {
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px dashed #eceff5;
  width: 100%;
}

.icon-tips {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 6px;
  margin-top: 8px;
}

.icon-label {
  font-size: 12px;
  color: var(--text-sub);
}

.icon-tag {
  cursor: pointer;
}
</style>
