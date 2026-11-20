import request from '@/utils/request'

/**
 * 分页查询用户列表
 *
 * @param {Object} params 查询参数
 * @returns {Promise} 请求结果
 */
export function pageUser(params) {
  return request.get('/admin/user/page', { params })
}

/**
 * 新增用户
 *
 * @param {Object} data 用户信息
 * @returns {Promise} 请求结果
 */
export function addUser(data) {
  return request.post('/admin/user', data)
}

/**
 * 修改用户
 *
 * @param {Number} id   用户ID
 * @param {Object} data 用户信息
 * @returns {Promise} 请求结果
 */
export function updateUser(id, data) {
  return request.put(`/admin/user/${id}`, data)
}

/**
 * 修改用户启用 / 禁用状态
 *
 * @param {Number} id 用户ID
 * @returns {Promise} 请求结果
 */
export function updateUserStatus(id) {
  return request.put(`/admin/user/status/${id}`)
}

/**
 * 删除用户
 *
 * @param {Number} id 用户ID
 * @returns {Promise} 请求结果
 */
export function deleteUser(id) {
  return request.delete(`/admin/user/${id}`)
}
