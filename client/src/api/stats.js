import request from '@/utils/request'

/**
 * 查询首页仪表盘全部统计数据
 *
 * @returns {Promise} 请求结果
 */
export function getDashboard() {
  return request.get('/stats/dashboard')
}
