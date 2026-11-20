import request from '@/utils/request'

/**
 * 分页查询宠物列表
 *
 * @param {Object} params 查询参数
 * @returns {Promise} 请求结果
 */
export function pagePet(params) {
  return request.get('/pet/list', { params })
}

/**
 * 查询宠物详情
 *
 * @param {Number} id 宠物ID
 * @returns {Promise} 请求结果
 */
export function getPetDetail(id) {
  return request.get(`/pet/detail/${id}`)
}

/**
 * 新增宠物
 *
 * @param {Object} data 宠物信息
 * @returns {Promise} 请求结果
 */
export function addPet(data) {
  return request.post('/pet', data)
}

/**
 * 修改宠物
 *
 * @param {Number} id   宠物ID
 * @param {Object} data 宠物信息
 * @returns {Promise} 请求结果
 */
export function updatePet(id, data) {
  return request.put(`/pet/${id}`, data)
}

/**
 * 修改宠物上下架状态
 *
 * @param {Number} id 宠物ID
 * @returns {Promise} 请求结果
 */
export function updatePetStatus(id) {
  return request.put(`/pet/status/${id}`)
}

/**
 * 删除宠物
 *
 * @param {Number} id 宠物ID
 * @returns {Promise} 请求结果
 */
export function deletePet(id) {
  return request.delete(`/pet/${id}`)
}
