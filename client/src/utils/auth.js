/**
 * 登录态工具类
 *
 * <p>统一管理 Token 与用户信息的本地存储。</p>
 */

// Token 存储键
const TOKEN_KEY = 'token'
// 用户信息存储键
const USER_KEY = 'user'

/**
 * 保存登录信息
 *
 * @param {Object} data 登录返回的数据（包含 token 与 user）
 */
export function setLoginInfo(data) {
  localStorage.setItem(TOKEN_KEY, data.token)
  localStorage.setItem(USER_KEY, JSON.stringify(data.user))
}

/**
 * 获取令牌
 *
 * @returns {string|null} 令牌
 */
export function getToken() {
  return localStorage.getItem(TOKEN_KEY)
}

/**
 * 获取当前登录用户信息
 *
 * @returns {Object|null} 用户信息
 */
export function getUser() {
  const raw = localStorage.getItem(USER_KEY)
  if (!raw) {
    return null
  }
  try {
    return JSON.parse(raw)
  } catch (e) {
    return null
  }
}

/**
 * 清除登录信息
 */
export function clearLoginInfo() {
  localStorage.removeItem(TOKEN_KEY)
  localStorage.removeItem(USER_KEY)
}
