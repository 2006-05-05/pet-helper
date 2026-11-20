import request from '@/utils/request'

/**
 * 上传图片文件
 *
 * @param {FormData} formData 包含 file 字段的表单数据
 * @returns {Promise} 请求结果
 */
export function uploadFile(formData) {
  return request.post('/file/upload', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}
