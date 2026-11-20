// 我的领养申请页
Page({
  data: {
    // 申请列表
    applies: [] as any[],
    // 是否加载中
    loading: false
  },

  /**
   * 页面显示
   */
  onShow() {
    const app = getApp()
    if (!app.checkLogin()) {
      this.setData({ applies: [] })
      return
    }
    this.loadData()
  },

  /**
   * 加载申请列表
   */
  loadData() {
    this.setData({ loading: true })
    import('../../utils/request').then(({ get, imageUrl }) => {
      get('/apply/my').then((res: any) => {
        const raw = res.data || []
        // 拼接图片完整地址
        const applies = raw.map((item: any) => {
          return Object.assign({}, item, { coverUrl: imageUrl(item.petCover) })
        })
        this.setData({ applies: applies, loading: false })
      }).catch(() => {
        this.setData({ loading: false })
      })
    })
  },

  /**
   * 取消申请
   *
   * @param {object} event 事件对象
   */
  cancelApply(event: any) {
    const id = event.currentTarget.dataset.id
    wx.showModal({
      title: '提示',
      content: '确定要取消这条领养申请吗？',
      success: (res) => {
        if (res.confirm) {
          import('../../utils/request').then(({ put }) => {
            put('/apply/cancel/' + id).then(() => {
              wx.showToast({ title: '已取消', icon: 'success' })
              this.loadData()
            })
          })
        }
      }
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
   * 根据状态返回中文描述
   *
   * @param {Number} status 状态值
   * @returns {string} 状态文本
   */
  statusText(status: number): string {
    return ['待审核', '已通过', '已驳回', '已取消'][status] || '未知'
  }
})
