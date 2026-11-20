// 我的收藏页
Page({
  data: {
    // 收藏的宠物列表
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
   * 加载收藏列表
   */
  loadData() {
    this.setData({ loading: true })
    import('../../utils/request').then(({ get, imageUrl }) => {
      get('/favorite/my').then((res: any) => {
        const raw = res.data || []
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
   * 跳转详情
   *
   * @param {object} event 事件对象
   */
  goDetail(event: any) {
    wx.navigateTo({ url: '/pages/detail/detail?id=' + event.currentTarget.dataset.id })
  },

  /**
   * 取消收藏
   *
   * @param {object} event 事件对象
   */
  removeFavorite(event: any) {
    const id = event.currentTarget.dataset.id
    import('../../utils/request').then(({ del }) => {
      del('/favorite/' + id).then(() => {
        wx.showToast({ title: '已取消收藏', icon: 'success' })
        this.loadData()
      })
    })
  }
})
