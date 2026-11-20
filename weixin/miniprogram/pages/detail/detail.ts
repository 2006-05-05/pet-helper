// 宠物详情页
Page({
  data: {
    // 宠物ID
    id: 0,
    // 宠物详情
    pet: null as any,
    // 相册图片地址列表
    images: [] as string[],
    // 当前图片索引
    current: 0,
    // 留言列表
    comments: [] as any[],
    // 留言输入内容
    commentText: '',
    // 领养申请弹窗显示状态
    applyVisible: false,
    // 领养申请表单
    applyForm: {
      reason: '',
      address: '',
      phone: '',
      experience: ''
    },
    // 是否已收藏
    favorited: false
  },

  /**
   * 页面加载
   *
   * @param {object} options 页面参数
   */
  onLoad(options: any) {
    const id = options.id
    if (!id) {
      wx.showToast({ title: '参数错误', icon: 'none' })
      return
    }
    this.setData({ id: Number(id) })
    this.loadDetail()
    this.loadComments()
  },

  /**
   * 加载宠物详情
   */
  loadDetail() {
    import('../../utils/request').then(({ get, imageUrl }) => {
      get('/pet/detail/' + this.data.id).then((res: any) => {
        const pet = res.data
        // 组装相册图片地址
        const list: string[] = []
        if (pet.coverImg) {
          list.push(imageUrl(pet.coverImg))
        }
        if (pet.images && pet.images.length) {
          pet.images.forEach((img: any) => {
            const url = imageUrl(img.url)
            if (list.indexOf(url) === -1) {
              list.push(url)
            }
          })
        }
        this.setData({
          pet: pet,
          images: list,
          favorited: pet.favoriteFlag === 1
        })
        wx.setNavigationBarTitle({ title: pet.name })
      })
    })
  },

  /**
   * 加载留言列表
   */
  loadComments() {
    import('../../utils/request').then(({ get }) => {
      get('/comment/list/' + this.data.id).then((res: any) => {
        this.setData({ comments: res.data || [] })
      })
    })
  },

  /**
   * 切换轮播图片
   *
   * @param {object} event 事件对象
   */
  onSwiperChange(event: any) {
    this.setData({ current: event.detail.current })
  },

  /**
   * 预览大图
   *
   * @param {object} event 事件对象
   */
  previewImage(event: any) {
    const current = event.currentTarget.dataset.src
    wx.previewImage({
      current: current,
      urls: this.data.images
    })
  },

  /**
   * 收藏 / 取消收藏
   */
  toggleFavorite() {
    const app = getApp()
    if (!app.checkLogin()) {
      return
    }
    import('../../utils/request').then(({ post, del }) => {
      const action = this.data.favorited ? del : post
      action('/favorite/' + this.data.id).then(() => {
        this.setData({ favorited: !this.data.favorited })
        wx.showToast({
          title: this.data.favorited ? '已收藏' : '已取消收藏',
          icon: 'success'
        })
      })
    })
  },

  /**
   * 打开领养申请弹窗
   */
  openApply() {
    const app = getApp()
    if (!app.checkLogin()) {
      return
    }
    // 回填手机号
    const userInfo = wx.getStorageSync('userInfo')
    this.setData({
      applyVisible: true,
      applyForm: {
        reason: '',
        address: '',
        phone: userInfo && userInfo.phone ? userInfo.phone : '',
        experience: ''
      }
    })
  },

  /**
   * 关闭领养申请弹窗
   */
  closeApply() {
    this.setData({ applyVisible: false })
  },

  /**
   * 阻止弹窗内容点击冒泡
   */
  noop() {},

  /**
   * 申请表单输入
   *
   * @param {object} event 事件对象
   */
  onApplyInput(event: any) {
    const field = event.currentTarget.dataset.field
    this.setData({ ['applyForm.' + field]: event.detail.value })
  },

  /**
   * 提交领养申请
   */
  submitApply() {
    const { reason, address, phone } = this.data.applyForm
    if (!reason.trim()) {
      wx.showToast({ title: '请填写领养理由', icon: 'none' })
      return
    }
    if (!address.trim()) {
      wx.showToast({ title: '请填写居住地址', icon: 'none' })
      return
    }
    if (!phone.trim()) {
      wx.showToast({ title: '请填写联系电话', icon: 'none' })
      return
    }
    import('../../utils/request').then(({ post }) => {
      post('/apply', {
        petId: this.data.id,
        reason: reason,
        address: address,
        phone: phone,
        experience: this.data.applyForm.experience
      }).then(() => {
        this.setData({ applyVisible: false })
        wx.showToast({ title: '提交成功，请等待审核', icon: 'success' })
      })
    })
  },

  /**
   * 输入留言内容
   *
   * @param {object} event 事件对象
   */
  onCommentInput(event: any) {
    this.setData({ commentText: event.detail.value })
  },

  /**
   * 提交留言
   */
  submitComment() {
    const app = getApp()
    if (!app.checkLogin()) {
      return
    }
    const content = this.data.commentText.trim()
    if (!content) {
      wx.showToast({ title: '请输入留言内容', icon: 'none' })
      return
    }
    import('../../utils/request').then(({ post }) => {
      post('/comment', { petId: this.data.id, content: content }).then(() => {
        this.setData({ commentText: '' })
        wx.showToast({ title: '留言成功', icon: 'success' })
        this.loadComments()
      })
    })
  },

  /**
   * 跳转到我的申请页
   */
  goMy() {
    wx.navigateTo({ url: '/pages/apply/apply' })
  },

  /**
   * 分享
   *
   * @returns {object} 分享配置
   */
  onShareAppMessage() {
    return {
      title: this.data.pet ? this.data.pet.name + '正在等待一个家' : '宠物领养',
      path: '/pages/detail/detail?id=' + this.data.id
    }
  }
})
