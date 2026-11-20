import request from '@/utils/request'

/**
 * 分页查询留言
 *
 * @param {Object} params 查询参数
 * @returns {Promise} 请求结果
 */
export function pageComment(params) {
  return request.get('/comment/page', { params })
}

/**
 * 删除留言
 *
 * @param {Number} id 留言ID
 * @returns {Promise} 请求结果
 */
export function deleteComment(id) {
  return request.delete(`/comment/${id}`)
}
