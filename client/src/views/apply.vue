<template>
  <!-- 领养申请审核页 -->
  <div class="page-container fade-in-up">
    <!-- 页面标题 -->
    <div class="page-header">
      <div>
        <div class="page-title">领养审核</div>
        <div class="page-desc">审核用户提交的领养申请，审核通过后宠物自动变更为已领养状态</div>
      </div>
    </div>

    <!-- 状态统计小标签 -->
    <div class="status-tabs">
      <div
        v-for="tab in tabs"
        :key="tab.value"
        class="status-tab"
        :class="{ active: query.status === tab.value }"
        @click="handleTabChange(tab.value)"
      >
        <span class="tab-name">{{ tab.label }}</span>
        <span class="tab-count">{{ tab.count }}</span>
      </div>
    </div>

    <!-- 申请表格 -->
    <div class="table-card">
      <el-table v-loading="loading" :data="list" style="width: 100%">
        <el-table-column label="宠物" min-width="160">
          <template #default="{ row }">
            <div class="pet-cell">
              <img class="pet-thumb" :src="row.petCover" :alt="row.petName" />
              <span>{{ row.petName }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="申请人" min-width="110">
          <template #default="{ row }">
            <div class="apply-user">
              <div class="apply-nickname">{{ row.userNickname }}</div>
              <div class="apply-account">@{{ row.username }}</div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="联系电话" prop="phone" width="120" />
        <el-table-column label="领养理由" prop="reason" min-width="220" show-overflow-tooltip />
        <el-table-column label="居住地址" prop="address" min-width="170" show-overflow-tooltip />
        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="statusType(row.status)" size="small" effect="light">
              {{ statusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="申请时间" prop="createTime" width="160" />
        <el-table-column label="操作" width="180" align="center" fixed="right">
          <template #default="{ row }">
            <template v-if="row.status === 0">
              <el-button type="success" link :icon="CircleCheck" @click="handleAudit(row, 1)">通过</el-button>
              <el-button type="danger" link :icon="CircleClose" @click="handleAudit(row, 2)">驳回</el-button>
            </template>
            <el-button type="info" link :icon="View" @click="handleDetail(row)">详情</el-button>
          </template>
        </el-table-column>
        <template #empty>
          <div class="empty-box">暂无领养申请数据</div>
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

    <!-- 审核弹窗 -->
    <el-dialog v-model="auditVisible" title="审核领养申请" width="560px" destroy-on-close>
      <div v-if="current" class="audit-body">
        <div class="audit-pet">
          <img class="audit-img" :src="current.petCover" :alt="current.petName" />
          <div>
            <div class="audit-name">{{ current.petName }}</div>
            <div class="audit-user">申请人：{{ current.userNickname }}（{{ current.username }}）</div>
          </div>
        </div>
        <el-descriptions :column="1" border size="small" class="audit-desc">
          <el-descriptions-item label="联系电话">{{ current.phone }}</el-descriptions-item>
          <el-descriptions-item label="居住地址">{{ current.address }}</el-descriptions-item>
          <el-descriptions-item label="领养理由">{{ current.reason }}</el-descriptions-item>
          <el-descriptions-item label="养宠经验">{{ current.experience || '未填写' }}</el-descriptions-item>
          <el-descriptions-item label="申请时间">{{ current.createTime }}</el-descriptions-item>
        </el-descriptions>
        <el-form label-width="90px" class="audit-form">
          <el-form-item label="审核结果">
            <el-radio-group v-model="auditForm.status">
              <el-radio-button :label="1">通过</el-radio-button>
              <el-radio-button :label="2">驳回</el-radio-button>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="审核备注">
            <el-input v-model="auditForm.remark" type="textarea" :rows="3" placeholder="请填写审核备注，将展示给申请人" />
          </el-form-item>
        </el-form>
      </div>
      <template #footer>
        <el-button @click="auditVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmitAudit">提交审核</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { CircleCheck, CircleClose, View } from '@element-plus/icons-vue'
import { pageApply, auditApply } from '@/api/apply'

// 表格加载状态
const loading = ref(false)
// 提交状态
const submitting = ref(false)
// 申请列表
const list = ref([])
// 总记录数
const total = ref(0)
// 审核弹窗显示状态
const auditVisible = ref(false)
// 当前审核的申请
const current = ref(null)

// 查询参数
const query = reactive({
  page: 1,
  pageSize: 10,
  status: 0
})

// 审核表单
const auditForm = reactive({
  status: 1,
  remark: ''
})

// 状态标签页
const tabs = reactive([
  { label: '待审核', value: 0, count: 0 },
  { label: '已通过', value: 1, count: 0 },
  { label: '已驳回', value: 2, count: 0 },
  { label: '已取消', value: 3, count: 0 },
  { label: '全部', value: '', count: 0 }
])

/**
 * 加载申请列表
 */
async function loadData() {
  loading.value = true
  try {
    const res = await pageApply(query)
    list.value = res.data.records || []
    total.value = res.data.total || 0
    // 更新标签页数量统计
    await loadTabCounts()
  } catch (e) {
    // 错误已由拦截器处理
  } finally {
    loading.value = false
  }
}

/**
 * 加载各状态的数量统计
 */
async function loadTabCounts() {
  // 并行请求各状态的数量统计
  const tasks = tabs.map((tab) => {
    if (tab.value === '') {
      return Promise.resolve()
    }
    return pageApply({ page: 1, pageSize: 1, status: tab.value }).then((res) => {
      tab.count = res.data.total || 0
    })
  })
  await Promise.all(tasks)
  const all = await pageApply({ page: 1, pageSize: 1 })
  tabs[tabs.length - 1].count = all.data.total || 0
}

/**
 * 切换状态标签
 *
 * @param {Number} value 状态值
 */
function handleTabChange(value) {
  query.status = value
  query.page = 1
  loadData()
}

/**
 * 打开审核弹窗
 *
 * @param {Object} row 当前申请
 * @param {Number} status 预选审核结果
 */
function handleAudit(row, status) {
  current.value = row
  auditForm.status = status
  auditForm.remark = status === 1 ? '资料齐全，符合领养条件' : ''
  auditVisible.value = true
}

/**
 * 查看申请详情
 *
 * @param {Object} row 当前申请
 */
function handleDetail(row) {
  current.value = row
  auditForm.status = row.status === 1 ? 1 : 2
  auditForm.remark = row.auditRemark || ''
  auditVisible.value = true
}

/**
 * 提交审核
 */
async function handleSubmitAudit() {
  submitting.value = true
  try {
    await auditApply(current.value.id, auditForm.status, auditForm.remark)
    ElMessage.success(auditForm.status === 1 ? '审核通过' : '已驳回')
    auditVisible.value = false
    loadData()
  } catch (e) {
    // 错误已由拦截器处理
  } finally {
    submitting.value = false
  }
}

/**
 * 根据状态返回标签类型
 *
 * @param {Number} status 状态值
 * @returns {String} 标签类型
 */
function statusType(status) {
  return ['warning', 'success', 'danger', 'info'][status] || 'info'
}

/**
 * 根据状态返回中文名称
 *
 * @param {Number} status 状态值
 * @returns {string} 状态名称
 */
function statusText(status) {
  return ['待审核', '已通过', '已驳回', '已取消'][status] || '未知'
}

onMounted(loadData)
</script>

<style scoped lang="scss">
/* 状态标签页 */
.status-tabs {
  display: flex;
  gap: 12px;
  margin-bottom: 16px;
  flex-wrap: wrap;
}

.status-tab {
  background: #fff;
  border-radius: 12px;
  padding: 12px 22px;
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  box-shadow: var(--shadow);
  transition: all 0.22s ease;
  border: 2px solid transparent;

  &:hover {
    transform: translateY(-3px);
  }

  &.active {
    border-color: var(--primary);
    background: linear-gradient(135deg, #fff6f1, #ffffff);
  }
}

.tab-name {
  font-size: 14px;
  color: var(--text-sub);
}

.tab-count {
  font-size: 17px;
  font-weight: 700;
  color: var(--text-main);
}

.status-tab.active .tab-name {
  color: var(--primary);
  font-weight: 600;
}

/* 申请人信息 */
.apply-user {
  line-height: 1.4;
}

.apply-nickname {
  font-size: 14px;
  color: var(--text-main);
}

.apply-account {
  font-size: 12px;
  color: var(--text-sub);
}

/* 审核弹窗 */
.audit-pet {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 14px;
  border-radius: 12px;
  background: linear-gradient(135deg, #f6f8fc, #eef6ff);
  margin-bottom: 16px;
}

.audit-img {
  width: 64px;
  height: 64px;
  border-radius: 14px;
  object-fit: cover;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.12);
}

.audit-name {
  font-size: 17px;
  font-weight: 700;
  color: var(--text-main);
}

.audit-user {
  font-size: 13px;
  color: var(--text-sub);
  margin-top: 5px;
}

.audit-desc {
  margin-bottom: 16px;
}

.audit-form {
  margin-top: 4px;
}
</style>
