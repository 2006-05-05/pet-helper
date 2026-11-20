// 我的发布页：展示当前登录用户发布的所有领养信息
Page({
  data: {
    // 我发布的宠物列表
    pets: [] as any[],
    // 是否加载中
    loading: false
  },

  /**
   * 页面显示
   */
  onShow() {
    const app = getApp()
    if (!app.checkLogin()) {
      this.setData({ pets: [] })
      return
    }
    this.loadData()
  },

  /**
   * 下拉刷新
   */
  onPullDownRefresh() {
    this.loadData()
    wx.stopPullDownRefresh()
  },

  /**
   * 加载我发布的宠物列表
   */
  loadData() {
    this.setData({ loading: true })
    import('../../utils/request').then(({ get, imageUrl }) => {
      get('/pet/my').then((res: any) => {
        const raw = res.data || []
        // 拼接图片完整地址
        const pets = raw.map((item: any) => {
          return Object.assign({}, item, { coverUrl: imageUrl(item.coverImg) })
        })
        this.setData({ pets: pets, loading: false })
      }).catch(() => {
        this.setData({ loading: false })
      })
    })
  },

  /**
   * 跳转宠物详情
   *
   * @param {object} event 事件对象
   */
  goDetail(event: any) {
    wx.navigateTo({ url: '/pages/detail/detail?id=' + event.currentTarget.dataset.id })
  },

  /**
   * 跳转发布页
   */
  goPublish() {
    wx.switchTab({ url: '/pages/publish/publish' })
  },

  /**
   * 根据宠物状态返回中文描述
   *
   * @param {Number} status 状态值：1 待领养 2 已领养 0 已下架
   * @returns {string} 状态文本
   */
  statusText(status: number): string {
    return ['已下架', '待领养', '已领养'][status] || '未知'
  }
})
