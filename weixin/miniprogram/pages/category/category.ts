// 分类筛选页
Page({
  data: {
    // 分类列表
    categories: [] as any[],
    // 当前选中的分类ID
    categoryId: 0 as number,
    // 性别筛选
    gender: '',
    // 城市筛选
    city: '',
    // 可选城市列表
    cities: ['北京', '上海', '广州', '深圳', '杭州', '成都', '南京', '武汉', '苏州', '长沙'] as string[],
    // 筛选结果列表
    pets: [] as any[],
    // 是否加载中
    loading: false,
    // 是否已加载完
    noMore: false,
    // 当前页码
    page: 1,
    // 每页条数
    pageSize: 10
  },

  /**
   * 页面加载
   */
  onLoad() {
    this.loadCategories()
    this.loadPets(true)
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
   * 加载分类
   */
  loadCategories() {
    import('../../utils/request').then(({ get }) => {
      get('/category/list').then((res: any) => {
        this.setData({ categories: res.data || [] })
      })
    })
  },

  /**
   * 加载筛选结果
   *
   * <p>只查询状态为「待领养」（status=1）的宠物。</p>
   *
   * @param {boolean} reset 是否重置分页
   */
  loadPets(reset: boolean) {
    const { page, pageSize, categoryId, gender, city } = this.data
    this.setData({ loading: true })
    import('../../utils/request').then(({ get, imageUrl }) => {
      const params: any = { page: reset ? 1 : page, pageSize: pageSize, status: 1 }
      if (categoryId) {
        params.categoryId = categoryId
      }
      if (gender) {
        params.gender = gender
      }
      if (city) {
        params.city = city
      }
      get('/pet/list', params).then((res: any) => {
        const raw = res.data.records || []
        const total = res.data.total || 0
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
   * 选择分类
   *
   * @param {object} event 事件对象
   */
  selectCategory(event: any) {
    this.setData({ categoryId: event.currentTarget.dataset.id, page: 1, noMore: false })
    this.loadPets(true)
  },

  /**
   * 选择性别
   *
   * @param {object} event 事件对象
   */
  selectGender(event: any) {
    this.setData({ gender: event.currentTarget.dataset.value, page: 1, noMore: false })
    this.loadPets(true)
  },

  /**
   * 选择城市
   *
   * @param {object} event 事件对象
   */
  selectCity(event: any) {
    this.setData({ city: event.currentTarget.dataset.value, page: 1, noMore: false })
    this.loadPets(true)
  },

  /**
   * 重置所有筛选条件
   */
  resetFilter() {
    this.setData({ categoryId: 0, gender: '', city: '', page: 1, noMore: false })
    this.loadPets(true)
  },

  /**
   * 跳转详情页
   *
   * @param {object} event 事件对象
   */
  goDetail(event: any) {
    wx.navigateTo({ url: '/pages/detail/detail?id=' + event.currentTarget.dataset.id })
  }
})
