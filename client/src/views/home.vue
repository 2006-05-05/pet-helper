<template>
  <!-- 数据首页：统计卡片 + 图表区 + 最新数据表格 -->
  <div class="page-container fade-in-up">
    <!-- 欢迎横幅 -->
    <div class="banner">
      <div class="banner-text">
        <h2>{{ greeting }}，{{ user.nickname || user.username }}！</h2>
        <p>今天也要用领养代替购买，让每一只小动物都能找到温暖的家 🐾</p>
      </div>
      <div class="banner-date">
        <div class="date-item">
          <span class="date-value">{{ overview.userTotal || 0 }}</span>
          <span class="date-label">注册用户</span>
        </div>
        <div class="date-item">
          <span class="date-value">{{ overview.petTotal || 0 }}</span>
          <span class="date-label">在册宠物</span>
        </div>
        <div class="date-item">
          <span class="date-value">{{ overview.applyPending || 0 }}</span>
          <span class="date-label">待审核申请</span>
        </div>
      </div>
    </div>

    <!-- 统计卡片 -->
    <el-row :gutter="16" class="stat-row">
      <el-col v-for="card in statCards" :key="card.key" :xs="12" :sm="12" :md="6">
        <div class="stat-card pet-card" :style="{ background: card.bg }">
          <div class="stat-icon" :style="{ background: card.iconBg }">{{ card.icon }}</div>
          <div class="stat-body">
            <div class="stat-value">{{ card.value }}<span class="stat-unit">{{ card.unit }}</span></div>
            <div class="stat-label">{{ card.label }}</div>
            <div class="stat-extra">{{ card.extra }}</div>
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- 图表区域 -->
    <el-row :gutter="16" class="chart-row">
      <el-col :xs="24" :md="12">
        <ChartCard title="宠物分类占比" :option="categoryOption" :height="300" />
      </el-col>
      <el-col :xs="24" :md="12">
        <ChartCard title="近 7 日领养申请趋势" :option="trendOption" :height="300" />
      </el-col>
    </el-row>

    <el-row :gutter="16" class="chart-row">
      <el-col :xs="24" :md="12">
        <ChartCard title="领养申请状态分布" :option="statusOption" :height="300" />
      </el-col>
      <el-col :xs="24" :md="12">
        <ChartCard title="各城市宠物数量 TOP10" :option="cityOption" :height="300" />
      </el-col>
    </el-row>

    <!-- 最新数据表格 -->
    <el-row :gutter="16" class="chart-row">
      <el-col :xs="24" :md="14">
        <div class="table-card">
          <div class="page-header">
            <div>
              <div class="page-title">最新领养申请</div>
              <div class="page-desc">展示最近提交的 6 条领养申请记录</div>
            </div>
          </div>
          <el-table :data="latestApply" style="width: 100%" size="default">
            <el-table-column label="宠物" min-width="110">
              <template #default="{ row }">
                <div class="pet-cell">
                  <img class="pet-thumb" :src="imageUrl(row.petCover)" :alt="row.petName" />
                  <span>{{ row.petName }}</span>
                </div>
              </template>
            </el-table-column>
            <el-table-column prop="userNickname" label="申请人" width="100" />
            <el-table-column label="状态" width="90">
              <template #default="{ row }">
                <el-tag :type="applyTagType(row.status)" size="small" effect="light">
                  {{ applyStatusText(row.status) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="申请时间" width="150" />
            <template #empty>
              <div class="empty-box">暂无领养申请数据</div>
            </template>
          </el-table>
        </div>
      </el-col>

      <el-col :xs="24" :md="10">
        <div class="table-card">
          <div class="page-header">
            <div>
              <div class="page-title">分类统计排行</div>
              <div class="page-desc">各分类下的宠物数量占比情况</div>
            </div>
          </div>
          <div class="rank-list">
            <div v-for="(item, index) in categoryData" :key="item.name" class="rank-item">
              <span class="rank-no" :class="{ top: index < 3 }">{{ index + 1 }}</span>
              <span class="rank-icon">{{ item.icon }}</span>
              <span class="rank-name">{{ item.name }}</span>
              <div class="rank-bar">
                <div class="rank-bar-inner" :style="{ width: barWidth(item.value) }"></div>
              </div>
              <span class="rank-value">{{ item.value }} 只</span>
            </div>
            <div v-if="!categoryData.length" class="empty-box">暂无分类数据</div>
          </div>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { getDashboard } from '@/api/stats'
import { getUser } from '@/utils/auth'
import ChartCard from '@/components/ChartCard.vue'

// 当前登录用户
const user = getUser() || {}
// 概览统计数据
const overview = ref({})
// 分类分布数据
const categoryData = ref([])
// 近 7 日趋势数据
const trendData = ref([])
// 申请状态数据
const statusData = ref([])
// 城市分布数据
const cityData = ref([])
// 最新申请数据
const latestApply = ref([])

/**
 * 根据当前时间生成问候语
 */
const greeting = computed(() => {
  const hour = new Date().getHours()
  if (hour < 6) return '夜深了'
  if (hour < 12) return '早上好'
  if (hour < 14) return '中午好'
  if (hour < 18) return '下午好'
  return '晚上好'
})

/**
 * 组装顶部统计卡片
 */
const statCards = computed(() => [
  {
    key: 'user',
    icon: '👥',
    label: '注册用户总数',
    value: overview.value.userTotal || 0,
    unit: ' 人',
    extra: `今日新增 ${overview.value.userToday || 0} 人`,
    bg: 'linear-gradient(135deg, #fff 0%, #f4f8ff 100%)',
    iconBg: 'linear-gradient(135deg, #597ef7, #85a5ff)'
  },
  {
    key: 'pet',
    icon: '🐶',
    label: '宠物信息总数',
    value: overview.value.petTotal || 0,
    unit: ' 只',
    extra: `待领养 ${overview.value.petWait || 0} 只 / 已领养 ${overview.value.petAdopted || 0} 只`,
    bg: 'linear-gradient(135deg, #fff 0%, #fff7f0 100%)',
    iconBg: 'linear-gradient(135deg, #ff7a45, #ff9c6e)'
  },
  {
    key: 'apply',
    icon: '📋',
    label: '领养申请总数',
    value: overview.value.applyTotal || 0,
    unit: ' 条',
    extra: `待审核 ${overview.value.applyPending || 0} 条`,
    bg: 'linear-gradient(135deg, #fff 0%, #f0fffb 100%)',
    iconBg: 'linear-gradient(135deg, #36cfc9, #5cdbd7)'
  },
  {
    key: 'rate',
    icon: '📈',
    label: '领养成功率',
    value: overview.value.applyRate || 0,
    unit: ' %',
    extra: `已通过 ${overview.value.applyPassed || 0} 条申请`,
    bg: 'linear-gradient(135deg, #fff 0%, #f6ffed 100%)',
    iconBg: 'linear-gradient(135deg, #52c41a, #95de64)'
  }
])

/**
 * 宠物分类占比饼图配置
 */
const categoryOption = computed(() => ({
  color: ['#ff7a45', '#36cfc9', '#faad14', '#597ef7'],
  tooltip: { trigger: 'item', formatter: '{b}: {c} 只 ({d}%)' },
  legend: { bottom: 0, itemWidth: 10, itemHeight: 10, textStyle: { color: '#5a6478' } },
  series: [
    {
      type: 'pie',
      radius: ['42%', '68%'],
      center: ['50%', '44%'],
      avoidLabelOverlap: true,
      itemStyle: { borderRadius: 8, borderColor: '#fff', borderWidth: 3 },
      label: { formatter: '{b}\n{c} 只', fontSize: 12, color: '#5a6478' },
      data: categoryData.value.map((item) => ({ name: item.name, value: item.value }))
    }
  ]
}))

/**
 * 近 7 日申请趋势图配置
 */
const trendOption = computed(() => ({
  color: ['#ff7a45', '#36cfc9'],
  tooltip: { trigger: 'axis' },
  legend: { bottom: 0, itemWidth: 12, itemHeight: 10, textStyle: { color: '#5a6478' } },
  grid: { left: 40, right: 20, top: 30, bottom: 46 },
  xAxis: {
    type: 'category',
    data: trendData.value.map((item) => item.date),
    axisLine: { lineStyle: { color: '#e4e7ed' } },
    axisLabel: { color: '#8a94a6' }
  },
  yAxis: {
    type: 'value',
    minInterval: 1,
    splitLine: { lineStyle: { color: '#f0f2f7' } },
    axisLabel: { color: '#8a94a6' }
  },
  series: [
    {
      name: '申请数量',
      type: 'bar',
      barWidth: 26,
      itemStyle: {
        borderRadius: [8, 8, 0, 0],
        color: echartsGradient()
      },
      data: trendData.value.map((item) => item.value)
    },
    {
      name: '累计申请',
      type: 'line',
      smooth: true,
      symbolSize: 7,
      lineStyle: { width: 3, color: '#36cfc9' },
      itemStyle: { color: '#36cfc9' },
      data: cumulative()
    }
  ]
}))

/**
 * 申请状态环形图配置
 */
const statusOption = computed(() => ({
  color: ['#faad14', '#52c41a', '#ff4d4f', '#8a94a6'],
  tooltip: { trigger: 'item', formatter: '{b}: {c} 条 ({d}%)' },
  legend: { bottom: 0, itemWidth: 10, itemHeight: 10, textStyle: { color: '#5a6478' } },
  series: [
    {
      type: 'pie',
      radius: ['46%', '70%'],
      center: ['50%', '44%'],
      itemStyle: { borderRadius: 8, borderColor: '#fff', borderWidth: 3 },
      label: { formatter: '{b}\n{c} 条', fontSize: 12, color: '#5a6478' },
      data: statusData.value.map((item) => ({ name: item.name, value: item.value }))
    }
  ]
}))

/**
 * 城市分布横向柱状图配置
 */
const cityOption = computed(() => ({
  color: ['#36cfc9'],
  tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
  grid: { left: 60, right: 24, top: 16, bottom: 30 },
  xAxis: {
    type: 'value',
    minInterval: 1,
    splitLine: { lineStyle: { color: '#f0f2f7' } },
    axisLabel: { color: '#8a94a6' }
  },
  yAxis: {
    type: 'category',
    data: cityData.value.map((item) => item.name).reverse(),
    axisLine: { show: false },
    axisTick: { show: false },
    axisLabel: { color: '#5a6478', fontSize: 12 }
  },
  series: [
    {
      name: '宠物数量',
      type: 'bar',
      barWidth: 16,
      itemStyle: {
        borderRadius: [0, 8, 8, 0],
        color: {
          type: 'linear',
          x: 0,
          y: 0,
          x2: 1,
          y2: 0,
          colorStops: [
            { offset: 0, color: '#85a5ff' },
            { offset: 1, color: '#36cfc9' }
          ]
        }
      },
      label: { show: true, position: 'right', color: '#5a6478', fontSize: 12 },
      data: cityData.value.map((item) => item.value).reverse()
    }
  ]
}))

/**
 * 计算累计申请数量
 *
 * @returns {Array} 累计值数组
 */
function cumulative() {
  let sum = 0
  return trendData.value.map((item) => {
    sum += item.value
    return sum
  })
}

/**
 * 渐变填充对象（供 ECharts 使用）
 */
function echartsGradient() {
  return {
    type: 'linear',
    x: 0,
    y: 0,
    x2: 0,
    y2: 1,
    colorStops: [
      { offset: 0, color: '#ffb185' },
      { offset: 1, color: '#ff7a45' }
    ]
  }
}

/**
 * 计算排行条宽度百分比
 *
 * @param {Number} value 数量
 * @returns {String} 百分比字符串
 */
function barWidth(value) {
  const max = Math.max(...categoryData.value.map((i) => i.value), 1)
  return (value / max) * 100 + '%'
}

/**
 * 根据状态值返回标签类型
 *
 * @param {Number} status 状态值
 * @returns {String} Element Plus 标签类型
 */
function applyTagType(status) {
  return ['warning', 'success', 'danger', 'info'][status] || 'info'
}

/**
 * 根据状态值返回中文名称
 *
 * @param {Number} status 状态值
 * @returns {string} 状态名称
 */
function applyStatusText(status) {
  return ['待审核', '已通过', '已驳回', '已取消'][status] || '未知'
}

/**
 * 拼接图片访问地址
 *
 * @param {String} path 图片相对路径
 * @returns {string} 完整访问地址
 */
function imageUrl(path) {
  if (!path) {
    return 'https://placehold.co/80x80/EBF0F7/9AA5B8?text=宠物'
  }
  if (path.startsWith('http')) {
    return path
  }
  return path
}

onMounted(async () => {
  const res = await getDashboard()
  const data = res.data || {}
  overview.value = data.overview || {}
  categoryData.value = data.category || []
  trendData.value = data.trend || []
  statusData.value = data.applyStatus || []
  cityData.value = data.city || []
  latestApply.value = data.latestApply || []
})
</script>

<style scoped lang="scss">
/* 欢迎横幅 */
.banner {
  border-radius: var(--radius);
  padding: 24px 28px;
  margin-bottom: 16px;
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: space-between;
  flex-wrap: wrap;
  gap: 16px;
  background: linear-gradient(120deg, #ff7a45 0%, #ff9c6e 40%, #36cfc9 100%);
  box-shadow: 0 12px 28px rgba(255, 122, 69, 0.28);

  h2 {
    font-size: 21px;
    margin-bottom: 7px;
    letter-spacing: 1px;
  }

  p {
    font-size: 13px;
    opacity: 0.92;
  }
}

.banner-date {
  display: flex;
  gap: 14px;
}

.date-item {
  background: rgba(255, 255, 255, 0.2);
  backdrop-filter: blur(6px);
  border-radius: 14px;
  padding: 12px 20px;
  display: flex;
  flex-direction: column;
  align-items: center;
  min-width: 96px;
}

.date-value {
  font-size: 23px;
  font-weight: 700;
  line-height: 1.2;
}

.date-label {
  font-size: 12px;
  opacity: 0.9;
  margin-top: 3px;
}

/* 统计卡片 */
.stat-row {
  margin-bottom: 16px;
}

.stat-card {
  display: flex;
  align-items: center;
  gap: 15px;
  padding: 20px;
  margin-bottom: 16px;
  border: 1px solid rgba(0, 0, 0, 0.04);
}

.stat-icon {
  width: 56px;
  height: 56px;
  flex-shrink: 0;
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 27px;
  box-shadow: 0 8px 18px rgba(0, 0, 0, 0.1);
}

.stat-value {
  font-size: 26px;
  font-weight: 800;
  color: var(--text-main);
  line-height: 1.2;
}

.stat-unit {
  font-size: 13px;
  font-weight: 500;
  color: var(--text-sub);
  margin-left: 2px;
}

.stat-label {
  font-size: 13px;
  color: var(--text-sub);
  margin-top: 3px;
}

.stat-extra {
  font-size: 11px;
  color: #b0b8c6;
  margin-top: 4px;
}

/* 图表行 */
.chart-row {
  margin-bottom: 16px;
}

/* 宠物单元格 */
.pet-cell {
  display: flex;
  align-items: center;
  gap: 9px;
}

/* 排行榜 */
.rank-list {
  padding: 4px 0 10px;
}

.rank-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 11px 0;
}

.rank-no {
  width: 24px;
  height: 24px;
  border-radius: 8px;
  background: #f1f3f8;
  color: #8a94a6;
  font-size: 12px;
  font-weight: 700;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;

  &.top {
    background: linear-gradient(135deg, #ff9c6e, #ff7a45);
    color: #fff;
    box-shadow: 0 4px 10px rgba(255, 122, 69, 0.35);
  }
}

.rank-icon {
  font-size: 19px;
}

.rank-name {
  font-size: 14px;
  color: var(--text-main);
  width: 62px;
  flex-shrink: 0;
}

.rank-bar {
  flex: 1;
  height: 8px;
  background: #f1f3f8;
  border-radius: 6px;
  overflow: hidden;
}

.rank-bar-inner {
  height: 100%;
  border-radius: 6px;
  background: linear-gradient(90deg, #ff9c6e, #36cfc9);
  transition: width 0.6s ease;
}

.rank-value {
  font-size: 13px;
  color: var(--text-sub);
  width: 54px;
  text-align: right;
  flex-shrink: 0;
}
</style>
