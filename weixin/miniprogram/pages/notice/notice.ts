// 领养公告页
Page({
  data: {
    // 公告列表
    notices: [] as any[],
    // 当前公告详情
    detail: null as any,
    // 是否显示详情弹层
    showDetail: false,
    // 是否加载中
    loading: false
  },

  /**
   * 页面加载
   */
  onLoad(options: any) {
    this.loadData()
    // 若携带 id 参数则直接展示详情
    if (options && options.id) {
      this.openDetailById(Number(options.id))
    }
  },

  /**
   * 加载公告列表
   */
  loadData() {
    this.setData({ loading: true })
    import('../../utils/request').then(({ get, imageUrl }) => {
      get('/notice/list', { page: 1, pageSize: 20 }).then((res: any) => {
        const raw = res.data.records || []
        const notices = raw.map((item: any) => {
          return Object.assign({}, item, { coverUrl: imageUrl(item.cover) })
        })
        this.setData({ notices: notices, loading: false })
      }).catch(() => {
        this.setData({ loading: false })
      })
    })
  },

  /**
   * 根据 ID 加载并展示公告详情
   *
   * @param {Number} id 公告ID
   */
  openDetailById(id: number) {
    import('../../utils/request').then(({ get, imageUrl }) => {
      get('/notice/detail/' + id).then((res: any) => {
        const detail = Object.assign({}, res.data, { coverUrl: imageUrl(res.data.cover) })
        this.setData({ detail: detail, showDetail: true })
      })
    })
  },

  /**
   * 点击公告卡片展示详情
   *
   * @param {object} event 事件对象
   */
  openDetail(event: any) {
    this.openDetailById(event.currentTarget.dataset.id)
  },

  /**
   * 关闭详情弹层
   */
  closeDetail() {
    this.setData({ showDetail: false })
  },

  /**
   * 阻止弹层内容冒泡
   */
  noop() {}
})
