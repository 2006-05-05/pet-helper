// 我的页面
Page({
  data: {
    // 当前登录用户信息
    user: null as any,
    // 我发布的宠物数量
    myPetCount: 0,
    // 我的申请数量
    myApplyCount: 0,
    // 我的收藏数量
    myFavoriteCount: 0,
    // 菜单项
    menus: [
      { key: 'publish', icon: '📤', title: '我的发布', desc: '查看我发布的领养信息', url: '/pages/mypet/mypet' },
      { key: 'apply', icon: '📋', title: '我的申请', desc: '查看领养申请进度', url: '/pages/apply/apply' },
      { key: 'favorite', icon: '❤️', title: '我的收藏', desc: '查看收藏的宠物', url: '/pages/favorite/favorite' },
      { key: 'notice', icon: '📰', title: '领养公告', desc: '查看平台公告与活动', url: '/pages/notice/notice' }
    ]
  },

  /**
   * 页面显示
   */
  onShow() {
    this.loadUser()
  },

  /**
   * 加载当前用户信息与统计数据
   */
  loadUser() {
    import('../../utils/auth').then(({ getUserInfo }) => {
      const user = getUserInfo()
      this.setData({ user: user })
      if (!user) {
        this.setData({ myPetCount: 0, myApplyCount: 0, myFavoriteCount: 0 })
        return
      }
      import('../../utils/request').then(({ get }) => {
        // 我的发布
        get('/pet/my').then((res: any) => {
          this.setData({ myPetCount: (res.data || []).length })
        })
        // 我的申请
        get('/apply/my').then((res: any) => {
          this.setData({ myApplyCount: (res.data || []).length })
        })
        // 我的收藏
        get('/favorite/my').then((res: any) => {
          this.setData({ myFavoriteCount: (res.data || []).length })
        })
      })
    })
  },

  /**
   * 跳转登录页
   */
  goLogin() {
    wx.navigateTo({ url: '/pages/login/login' })
  },

  /**
   * 跳转到登录态页面（先校验登录）
   *
   * @param {object} event 事件对象
   */
  goMenu(event: any) {
    const app = getApp()
    if (!app.checkLogin()) {
      return
    }
    wx.navigateTo({ url: event.currentTarget.dataset.url })
  },

  /**
   * 退出登录
   */
  logout() {
    wx.showModal({
      title: '提示',
      content: '确定要退出登录吗？',
      success: (res) => {
        if (res.confirm) {
          import('../../utils/auth').then(({ clearLoginInfo }) => {
            clearLoginInfo()
            this.setData({ user: null, myPetCount: 0, myApplyCount: 0, myFavoriteCount: 0 })
            wx.showToast({ title: '已退出登录', icon: 'success' })
          })
        }
      }
    })
  }
})
