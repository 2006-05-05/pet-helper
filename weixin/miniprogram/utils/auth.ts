/**
 * 登录态工具
 *
 * <p>统一管理小程序端的令牌与用户信息缓存。</p>
 */

/**
 * 保存登录信息
 *
 * @param {object} data 登录返回的数据
 */
export function setLoginInfo(data: any) {
  wx.setStorageSync('token', data.token)
  wx.setStorageSync('userInfo', data.user)
}

/**
 * 获取令牌
 *
 * @returns {string} 令牌
 */
export function getToken(): string {
  return wx.getStorageSync('token') || ''
}

/**
 * 获取当前登录用户信息
 *
 * @returns {object} 用户信息
 */
export function getUserInfo(): any {
  return wx.getStorageSync('userInfo') || null
}

/**
 * 清除登录信息
 */
export function clearLoginInfo() {
  wx.removeStorageSync('token')
  wx.removeStorageSync('userInfo')
}
