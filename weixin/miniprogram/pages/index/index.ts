// 首页
Page({
  data: {
    // 分类列表
    categories: [] as any[],
    // 公告列表
    notices: [] as any[],
    // 宠物列表
    pets: [] as any[],
    // 平台统计数据（与后台首页共用 /api/stats/overview 接口）
    stats: {
      petTotal: 0,
      petWait: 0,
      petAdopted: 0,
      applyTotal: 0
    } as any,
    // 搜索关键字
    keyword: '',
    // 当前分类ID
    currentCategory: 0 as number,
    // 是否加载中
    loading: false,
    // 是否已加载完全部数据
    noMore: false,
    // 当前页码
    page: 1,
    // 每页条数
    pageSize: 6
  },

  /**
   * 页面加载
   */
  onLoad() {
    this.loadCategories()
    this.loadNotices()
    this.loadPets(true)
    this.loadStats()
  },

  /**
   * 加载平台统计数据
   *
   * <p>与后台管理端共用同一个统计接口，保证小程序与后台展示的数据一致。</p>
   */
  loadStats() {
    import('../../utils/request').then(({ get }) => {
      get('/stats/overview').then((res: any) => {
        const data = res.data || {}
        this.setData({
          stats: {
            petTotal: data.petTotal || 0,
            petWait: data.petWait || 0,
            petAdopted: data.petAdopted || 0,
            applyTotal: data.applyTotal || 0
          }
        })
      })
    })
  },

  /**
   * 下拉刷新
   */
  onPullDownRefresh() {
    this.setData({ page: 1, noMore: false })
    this.loadPets(true)
    wx.stopPullDownRefresh()
  },

  /**
   * 页面触底加载更多
   */
  onReachBottom() {
    if (!this.data.noMore && !this.data.loading) {
      this.loadPets(false)
    }
  },

  /**
   * 加载宠物分类
   */
  loadCategories() {
    import('../../utils/request').then(({ get }) => {
      get('/category/list').then((res: any) => {
        this.setData({ categories: res.data || [] })
      })
    })
  },

  /**
   * 加载公告轮播
   */
  loadNotices() {
    import('../../utils/request').then(({ get }) => {
      get('/notice/list', { page: 1, pageSize: 5 }).then((res: any) => {
        this.setData({ notices: res.data.records || [] })
      })
    })
  },

  /**
   * 加载宠物列表
   *
   * <p>只查询状态为「待领养」（status=1）的宠物，已领养与已下架的宠物不在此处展示。</p>
   *
   * @param {boolean} reset 是否重新从第一页开始
   */
  loadPets(reset: boolean) {
    const { page, pageSize, keyword, currentCategory } = this.data
    this.setData({ loading: true })
    import('../../utils/request').then(({ get, imageUrl }) => {
      const params: any = {
        page: reset ? 1 : page,
        pageSize: pageSize,
        status: 1
      }
      if (keyword) {
        params.keyword = keyword
      }
      if (currentCategory) {
        params.categoryId = currentCategory
      }
      get('/pet/list', params).then((res: any) => {
        const raw = res.data.records || []
        const total = res.data.total || 0
        // 拼接图片的完整访问地址
        const records = raw.map((item: any) => {
          return Object.assign({}, item, { coverUrl: imageUrl(item.coverImg) })
        })
        const list = reset ? records : this.data.pets.concat(records)
        this.setData({
          pets: list,
          page: reset ? 1 : page + 1,
          noMore: list.length >= total,
          loading: false
        })
      }).catch(() => {
        this.setData({ loading: false })
      })
    })
  },

  /**
   * 切换分类筛选
   *
   * @param {object} event 点击事件
   */
  onCategoryTap(event: any) {
    const id = event.currentTarget.dataset.id
    this.setData({ currentCategory: id, page: 1, noMore: false })
    this.loadPets(true)
  },

  /**
   * 搜索关键字输入
   *
   * @param {object} event 输入事件
   */
  onSearchInput(event: any) {
    this.setData({ keyword: event.detail.value })
  },

  /**
   * 确认搜索
   */
  onSearchConfirm() {
    this.setData({ page: 1, noMore: false })
    this.loadPets(true)
  },

  /**
   * 跳转宠物详情页
   *
   * @param {object} event 点击事件
   */
  goDetail(event: any) {
    const id = event.currentTarget.dataset.id
    wx.navigateTo({ url: '/pages/detail/detail?id=' + id })
  },

  /**
   * 跳转公告详情页
   *
   * @param {object} event 点击事件
   */
  goNotice(event: any) {
    const id = event.currentTarget.dataset.id
    wx.navigateTo({ url: '/pages/notice/notice?id=' + id })
  },

  /**
   * 跳转分类页
   */
  goCategory() {
    wx.switchTab({ url: '/pages/category/category' })
  },

  /**
   * 跳转发布页
   */
  goPublish() {
    wx.switchTab({ url: '/pages/publish/publish' })
  }
})
