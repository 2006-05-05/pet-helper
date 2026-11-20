import request from '@/utils/request'

/**
 * 查询全部分类
 *
 * @returns {Promise} 请求结果
 */
export function listCategory() {
  return request.get('/category/list')
}

/**
 * 新增分类
 *
 * @param {Object} data 分类信息
 * @returns {Promise} 请求结果
 */
export function addCategory(data) {
  return request.post('/category', data)
}

/**
 * 修改分类
 *
 * @param {Number} id   分类ID
 * @param {Object} data 分类信息
 * @returns {Promise} 请求结果
 */
export function updateCategory(id, data) {
  return request.put(`/category/${id}`, data)
}

/**
 * 删除分类
 *
 * @param {Number} id 分类ID
 * @returns {Promise} 请求结果
 */
export function deleteCategory(id) {
  return request.delete(`/category/${id}`)
}
