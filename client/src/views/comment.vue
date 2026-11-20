<template>
  <!-- 留言管理页 -->
  <div class="page-container fade-in-up">
    <!-- 页面标题 -->
    <div class="page-header">
      <div>
        <div class="page-title">留言管理</div>
        <div class="page-desc">查看与维护用户在宠物详情页发表的留言评论</div>
      </div>
    </div>

    <!-- 搜索栏 -->
    <div class="search-bar">
      <el-form :inline="true" @submit.prevent>
        <el-form-item label="内容关键字">
          <el-input v-model="query.keyword" placeholder="请输入留言内容" clearable style="width: 220px" @keyup.enter="handleSearch" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleSearch">查询</el-button>
          <el-button :icon="Refresh" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 留言表格 -->
    <div class="table-card">
      <el-table v-loading="loading" :data="list" style="width: 100%">
        <el-table-column label="ID" prop="id" width="60" align="center" />
        <el-table-column label="留言人" min-width="150">
          <template #default="{ row }">
            <div class="user-cell">
              <el-avatar :size="32" class="user-avatar">{{ (row.nickname || '匿').charAt(0) }}</el-avatar>
              <span>{{ row.nickname || '匿名用户' }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="所属宠物" prop="petId" width="100" align="center">
          <template #default="{ row }">
            <el-tag size="small" effect="plain">#{{ row.petId }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="留言内容" prop="content" min-width="280" show-overflow-tooltip />
        <el-table-column label="状态" width="90" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small" effect="light">
              {{ row.status === 1 ? '显示中' : '已隐藏' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="留言时间" prop="createTime" width="170" />
        <el-table-column label="操作" width="100" align="center" fixed="right">
          <template #default="{ row }">
            <el-button type="danger" link :icon="Delete" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
        <template #empty>
          <div class="empty-box">暂无留言数据</div>
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
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Delete } from '@element-plus/icons-vue'
import { pageComment, deleteComment } from '@/api/comment'

// 表格加载状态
const loading = ref(false)
// 留言列表
const list = ref([])
// 总记录数
const total = ref(0)

// 查询参数
const query = reactive({
  page: 1,
  pageSize: 10,
  keyword: ''
})

/**
 * 加载留言列表
 */
async function loadData() {
  loading.value = true
  try {
    const res = await pageComment(query)
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
  query.page = 1
  loadData()
}

/**
 * 删除留言
 *
 * @param {Object} row 当前留言
 */
async function handleDelete(row) {
  try {
    await ElMessageBox.confirm(`确定要删除该留言吗？`, '提示', { type: 'warning' })
    await deleteComment(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch (e) {
    // 用户取消
  }
}

onMounted(loadData)
</script>

<style scoped lang="scss">
.user-cell {
  display: flex;
  align-items: center;
  gap: 9px;
}

.user-avatar {
  background: linear-gradient(135deg, #85a5ff, #597ef7);
  color: #fff;
  font-weight: 600;
}
</style>
