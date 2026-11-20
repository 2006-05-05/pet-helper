import request from '@/utils/request'

/**
 * 登录接口
 *
 * @param {Object} data 登录参数 { username, password }
 * @returns {Promise} 请求结果
 */
export function login(data) {
  return request.post('/auth/login', data)
}

/**
 * 注册接口
 *
 * @param {Object} data 注册参数
 * @returns {Promise} 请求结果
 */
export function register(data) {
  return request.post('/auth/register', data)
}

/**
 * 获取当前登录用户信息
 *
 * @returns {Promise} 请求结果
 */
export function getUserInfo() {
  return request.get('/auth/info')
}
