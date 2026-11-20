import request from '@/utils/request'

/**
 * 分页查询领养申请
 *
 * @param {Object} params 查询参数
 * @returns {Promise} 请求结果
 */
export function pageApply(params) {
  return request.get('/apply/page', { params })
}

/**
 * 审核领养申请
 *
 * @param {Number} id     申请ID
 * @param {Number} status 审核状态：1 通过 2 驳回
 * @param {String} remark 审核备注
 * @returns {Promise} 请求结果
 */
export function auditApply(id, status, remark) {
  return request.put(`/apply/audit/${id}`, null, { params: { status, remark } })
}
