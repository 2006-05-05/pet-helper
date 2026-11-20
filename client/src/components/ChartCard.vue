<template>
  <!-- 图表卡片组件：统一的标题、圆角与阴影样式 -->
  <div class="chart-card pet-card">
    <!-- 卡片头部 -->
    <div class="chart-header">
      <div class="chart-title">
        <span class="title-bar"></span>
        {{ title }}
      </div>
      <!-- 右上角附加内容插槽 -->
      <slot name="extra"></slot>
    </div>
    <!-- 图表容器 -->
    <div ref="chartRef" class="chart-body" :style="{ height: height + 'px' }"></div>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, watch } from 'vue'
import * as echarts from 'echarts'

// 组件属性
const props = defineProps({
  // 图表标题
  title: { type: String, required: true },
  // ECharts 配置项
  option: { type: Object, required: true },
  // 图表高度（像素）
  height: { type: Number, default: 300 }
})

// 图表 DOM 引用
const chartRef = ref(null)
// ECharts 实例
let chart = null

/**
 * 初始化图表
 */
function initChart() {
  if (!chartRef.value) {
    return
  }
  chart = echarts.init(chartRef.value)
  chart.setOption(props.option)
  // 监听窗口尺寸变化自适应
  window.addEventListener('resize', resizeChart)
}

/**
 * 重新设置图表配置
 */
function renderChart() {
  if (chart) {
    chart.setOption(props.option, true)
  }
}

/**
 * 窗口尺寸变化时调整图表
 */
function resizeChart() {
  if (chart) {
    chart.resize()
  }
}

onMounted(() => {
  initChart()
})

// 配置变化时重绘
watch(() => props.option, renderChart, { deep: true })

onBeforeUnmount(() => {
  window.removeEventListener('resize', resizeChart)
  if (chart) {
    chart.dispose()
    chart = null
  }
})
</script>

<style scoped lang="scss">
.chart-card {
  padding: 18px 20px 10px;
}

.chart-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 10px;
}

.chart-title {
  font-size: 15px;
  font-weight: 700;
  color: var(--text-main);
  display: flex;
  align-items: center;
  gap: 9px;

  .title-bar {
    width: 4px;
    height: 16px;
    border-radius: 2px;
    background: linear-gradient(180deg, var(--primary), var(--secondary));
  }
}

.chart-body {
  width: 100%;
}
</style>
