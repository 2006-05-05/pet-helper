// 小程序入口文件
App<IAppOption>({
  globalData: {
    // 后端服务地址，发布时请改为正式域名
    baseUrl: 'http://localhost:8080',
    // 当前登录用户信息
    userInfo: null
  },

  onLaunch() {
    // 读取本地缓存的登录信息
    const token = wx.getStorageSync('token')
    const userInfo = wx.getStorageSync('userInfo')
    if (token && userInfo) {
      this.globalData.userInfo = userInfo
    }
    console.log('小程序启动完成，Java1234 宠物领养系统')
  },

  /**
   * 检查用户是否已登录
   *
   * @returns {boolean} 是否已登录
   */
  checkLogin() {
    const token = wx.getStorageSync('token')
    if (!token) {
      wx.showToast({
        title: '请先登录',
        icon: 'none'
      })
      setTimeout(() => {
        wx.navigateTo({ url: '/pages/login/login' })
      }, 600)
      return false
    }
    return true
  }
})
