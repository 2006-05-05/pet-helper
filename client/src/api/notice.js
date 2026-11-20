import request from '@/utils/request'

/**
 * 分页查询公告
 *
 * @param {Object} params 查询参数
 * @returns {Promise} 请求结果
 */
export function pageNotice(params) {
  return request.get('/notice/list', { params })
}

/**
 * 查询公告详情
 *
 * @param {Number} id 公告ID
 * @returns {Promise} 请求结果
 */
export function getNoticeDetail(id) {
  return request.get(`/notice/detail/${id}`)
}

/**
 * 新增公告
 *
 * @param {Object} data 公告信息
 * @returns {Promise} 请求结果
 */
export function addNotice(data) {
  return request.post('/notice', data)
}

/**
 * 修改公告
 *
 * @param {Number} id   公告ID
 * @param {Object} data 公告信息
 * @returns {Promise} 请求结果
 */
export function updateNotice(id, data) {
  return request.put(`/notice/${id}`, data)
}

/**
 * 删除公告
 *
 * @param {Number} id 公告ID
 * @returns {Promise} 请求结果
 */
export function deleteNotice(id) {
  return request.delete(`/notice/${id}`)
}
