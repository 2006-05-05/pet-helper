import axios from 'axios'
import { ElMessage } from 'element-plus'

/**
 * axios 请求封装
 *
 * <p>统一注入 Token、统一处理业务状态码与异常提示。</p>
 */

// 创建 axios 实例
const request = axios.create({
  baseURL: '/api',
  timeout: 15000
})

// 请求拦截器：注入登录令牌
request.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = 'Bearer ' + token
    }
    // 过滤掉值为空字符串的查询参数，避免后端按空值做等值筛选
    if (config.params) {
      const params = {}
      Object.keys(config.params).forEach((key) => {
        const value = config.params[key]
        if (value !== '' && value !== null && value !== undefined) {
          params[key] = value
        }
      })
      config.params = params
    }
    return config
  },
  (error) => Promise.reject(error)
)

// 响应拦截器：统一处理业务结果
request.interceptors.response.use(
  (response) => {
    const res = response.data
    // 后端统一返回结构 { code, msg, data }
    if (res.code === 200) {
      return res
    }
    // 401 表示登录失效
    if (res.code === 401) {
      localStorage.removeItem('token')
      localStorage.removeItem('user')
      ElMessage.error('登录状态已失效，请重新登录')
      // 跳转到登录页
      window.location.hash = '#/login'
      return Promise.reject(new Error(res.msg))
    }
    ElMessage.error(res.msg || '操作失败')
    return Promise.reject(new Error(res.msg || '操作失败'))
  },
  (error) => {
    ElMessage.error(error.message || '网络异常，请稍后重试')
    return Promise.reject(error)
  }
)

export default request
