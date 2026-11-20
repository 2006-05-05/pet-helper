/**
 * 网络请求封装
 *
 * <p>统一拼接后端地址、注入 Token、处理业务状态码与异常提示。</p>
 */

/** 后端服务基础地址 */
const BASE_URL = 'http://localhost:8081'

/**
 * 发起 GET 请求
 *
 * @param {string} url 接口路径（不含 /api 前缀）
 * @param {object} data 查询参数
 * @returns {Promise} 请求结果
 */
export function get(url: string, data?: object) {
  return request(url, 'GET', data)
}

/**
 * 发起 POST 请求
 *
 * @param {string} url 接口路径
 * @param {object} data 请求参数
 * @returns {Promise} 请求结果
 */
export function post(url: string, data?: object) {
  return request(url, 'POST', data)
}

/**
 * 发起 PUT 请求
 *
 * @param {string} url 接口路径
 * @param {object} data 请求参数
 * @returns {Promise} 请求结果
 */
export function put(url: string, data?: object) {
  return request(url, 'PUT', data)
}

/**
 * 发起 DELETE 请求
 *
 * @param {string} url 接口路径
 * @param {object} data 请求参数
 * @returns {Promise} 请求结果
 */
export function del(url: string, data?: object) {
  return request(url, 'DELETE', data)
}

/**
 * 核心请求方法
 *
 * @param {string} url 接口路径
 * @param {string} method 请求方法
 * @param {object} data 请求参数
 * @returns {Promise} 请求结果
 */
function request(url: string, method: string, data?: object) {
  return new Promise((resolve, reject) => {
    // 从本地缓存读取登录令牌
    const token = wx.getStorageSync('token')
    wx.request({
      url: BASE_URL + '/api' + url,
      method: method as 'GET' | 'POST' | 'PUT' | 'DELETE',
      data: data as any,
      header: {
        'Content-Type': 'application/json',
        'Authorization': token ? 'Bearer ' + token : ''
      },
      success: (res) => {
        const body = res.data as any
        // 业务成功
        if (body && body.code === 200) {
          resolve(body)
          return
        }
        // 登录失效
        if (body && body.code === 401) {
          wx.removeStorageSync('token')
          wx.removeStorageSync('userInfo')
          wx.showToast({ title: '请重新登录', icon: 'none' })
          setTimeout(() => {
            wx.navigateTo({ url: '/pages/login/login' })
          }, 600)
          reject(body)
          return
        }
        // 其它业务错误
        wx.showToast({
          title: (body && body.msg) || '操作失败',
          icon: 'none',
          duration: 2000
        })
        reject(body)
      },
      fail: () => {
        wx.showToast({
          title: '网络异常，请检查后端服务是否启动',
          icon: 'none',
          duration: 2000
        })
        reject({ code: 500, msg: '网络异常' })
      }
    })
  })
}

/**
 * 上传图片到服务器
 *
 * @param {string} filePath 本地图片路径
 * @returns {Promise} 上传结果，返回文件访问地址
 */
export function uploadImage(filePath: string): Promise<string> {
  return new Promise<string>((resolve, reject) => {
    const token = wx.getStorageSync('token')
    wx.uploadFile({
      url: BASE_URL + '/api/file/upload',
      filePath: filePath,
      name: 'file',
      header: {
        'Authorization': token ? 'Bearer ' + token : ''
      },
      success: (res) => {
        try {
          const body = JSON.parse(res.data)
          if (body.code === 200) {
            resolve(body.data.url)
          } else {
            wx.showToast({ title: body.msg || '上传失败', icon: 'none' })
            reject(body)
          }
        } catch (e) {
          wx.showToast({ title: '上传失败', icon: 'none' })
          reject(e)
        }
      },
      fail: () => {
        wx.showToast({ title: '上传失败', icon: 'none' })
        reject({ code: 500, msg: '上传失败' })
      }
    })
  })
}

/**
 * 拼接完整图片访问地址
 *
 * @param {string} path 图片相对路径
 * @returns {string} 完整地址
 */
export function imageUrl(path: string): string {
  if (!path) {
    return ''
  }
  if (path.startsWith('http')) {
    return path
  }
  return BASE_URL + path
}
